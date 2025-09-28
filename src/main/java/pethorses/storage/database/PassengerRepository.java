package pethorses.storage.database;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class PassengerRepository {
    private final DatabaseManager dbManager;
    private final Logger logger;

    public PassengerRepository(DatabaseManager dbManager, Logger logger) {
        this.dbManager = dbManager;
        this.logger = logger;
        initializeTable();
    }

    private void initializeTable() {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS pet_horse_passengers (" +
                    "owner_uuid VARCHAR(36)," +
                    "passenger_uuid VARCHAR(36)," +
                    "PRIMARY KEY (owner_uuid, passenger_uuid))");
        } catch (SQLException e) {
            logger.severe("Error initializing passenger table: " + e.getMessage());
        }
    }

    public Map<UUID, Set<UUID>> loadAll() {
        Map<UUID, Set<UUID>> permissions = new HashMap<>();
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM pet_horse_passengers")) {
            while (rs.next()) {
                UUID ownerId = UUID.fromString(rs.getString("owner_uuid"));
                UUID passengerId = UUID.fromString(rs.getString("passenger_uuid"));
                permissions.computeIfAbsent(ownerId, k -> new HashSet<>()).add(passengerId);
            }
        } catch (SQLException e) {
            logger.severe("Error loading passengers: " + e.getMessage());
        }
        return permissions;
    }

    public void addPassenger(UUID ownerId, UUID passengerId) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO pet_horse_passengers (owner_uuid, passenger_uuid) VALUES (?, ?)")) {
            stmt.setString(1, ownerId.toString());
            stmt.setString(2, passengerId.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.warning("Error adding passenger: " + e.getMessage());
        }
    }

    public void removePassenger(UUID ownerId, UUID passengerId) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM pet_horse_passengers WHERE owner_uuid = ? AND passenger_uuid = ?")) {
            stmt.setString(1, ownerId.toString());
            stmt.setString(2, passengerId.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.warning("Error removing passenger: " + e.getMessage());
        }
    }
}
