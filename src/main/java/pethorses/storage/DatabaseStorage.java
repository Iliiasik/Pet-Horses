package pethorses.storage;

import pethorses.PetHorses;
import pethorses.config.ConfigManager;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;


public class DatabaseStorage implements StorageStrategy {
    private final PetHorses plugin;
    private final Logger logger;
    private Connection connection;
    private final String dbHost;
    private final int dbPort;
    private final String dbName;
    private final String dbUser;
    private final String dbPassword;

    private final Map<UUID, HorseData> horsesData = new HashMap<>();
    private final Map<UUID, Set<UUID>> passengerPermissions = new HashMap<>();

    public DatabaseStorage(PetHorses plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.dbHost = configManager.getDatabaseHost();
        this.dbPort = configManager.getDatabasePort();
        this.dbName = configManager.getDatabaseName();
        this.dbUser = configManager.getDatabaseUser();
        this.dbPassword = configManager.getDatabasePassword();
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            String url = "jdbc:mariadb://" + dbHost + ":" + dbPort + "/" + dbName;
            this.connection = DriverManager.getConnection(url, dbUser, dbPassword);
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS pet_horses (" +
                        "player_uuid VARCHAR(36) PRIMARY KEY," +
                        "level INT NOT NULL DEFAULT 1," +
                        "experience INT NOT NULL DEFAULT 0," +
                        "color VARCHAR(20) NOT NULL DEFAULT 'BROWN'," +
                        "style VARCHAR(20) NOT NULL DEFAULT 'NONE'," +
                        "horse_name VARCHAR(32)," +
                        "death_time BIGINT NOT NULL DEFAULT 0," +
                        "jumps INT NOT NULL DEFAULT 0," +
                        "blocks_traveled DOUBLE NOT NULL DEFAULT 0.0," +
                        "total_jumps INT NOT NULL DEFAULT 0," +
                        "total_blocks_traveled DOUBLE NOT NULL DEFAULT 0.0," +
                        "backpack_data LONGBLOB," +
                        "armor_data LONGBLOB" +
                        ")");
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS pet_horse_passengers (" +
                        "owner_uuid VARCHAR(36)," +
                        "passenger_uuid VARCHAR(36)," +
                        "PRIMARY KEY (owner_uuid, passenger_uuid))");
            }
        } catch (Exception e) {
            logger.severe("Database initialization error: " + e.getMessage());
        }
    }

    @Override
    public void loadData() {
        loadHorsesFromDatabase();
        loadPassengersFromDatabase();
    }

    private void loadHorsesFromDatabase() {
        if (connection == null) return;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM pet_horses")) {
            while (rs.next()) {
                UUID playerId = UUID.fromString(rs.getString("player_uuid"));
                HorseData data = new HorseData();
                data.setOwnerId(playerId);
                data.setLevel(rs.getInt("level"));
                data.setExperience(rs.getInt("experience"));
                data.setColor(parseColor(rs.getString("color")));
                data.setStyle(parseStyle(rs.getString("style")));
                data.setHorseName(rs.getString("horse_name"));
                data.setDeathTime(rs.getLong("death_time"));
                data.setJumps(rs.getInt("jumps"));
                data.setBlocksTraveled(rs.getDouble("blocks_traveled"));
                data.setTotalJumps(rs.getInt("total_jumps"));
                data.setTotalBlocksTraveled(rs.getDouble("total_blocks_traveled"));

                byte[] backpackData = rs.getBytes("backpack_data");
                if (backpackData != null && backpackData.length > 0) {
                    data.setBackpackItems(deserializeItemStacks(backpackData));
                }

                byte[] armorData = rs.getBytes("armor_data");
                if (armorData != null && armorData.length > 0) {
                    ItemStack[] armor = deserializeItemStacks(armorData);
                    if (armor.length > 0) {
                        data.setArmorItem(armor[0]);
                    }
                }

                horsesData.put(playerId, data);
            }
        } catch (SQLException e) {
            logger.severe("Error loading horse data from DB: " + e.getMessage());
        }
    }

    private void loadPassengersFromDatabase() {
        if (connection == null) return;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM pet_horse_passengers")) {
            while (rs.next()) {
                UUID ownerId = UUID.fromString(rs.getString("owner_uuid"));
                UUID passengerId = UUID.fromString(rs.getString("passenger_uuid"));
                passengerPermissions.computeIfAbsent(ownerId, k -> new HashSet<>()).add(passengerId);
            }
        } catch (SQLException e) {
            logger.severe("Error loading passenger data from DB: " + e.getMessage());
        }
    }

    @Override
    public HorseData getHorseData(UUID playerId) {
        return horsesData.computeIfAbsent(playerId, k -> new HorseData());
    }

    @Override
    public void saveHorseData(HorseData data) {
        if (connection == null || data.getOwnerId() == null) return;
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO pet_horses (player_uuid, level, experience, color, style, horse_name, " +
                        "death_time, jumps, blocks_traveled, total_jumps, total_blocks_traveled, backpack_data, armor_data) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "level = VALUES(level), experience = VALUES(experience), color = VALUES(color), " +
                        "style = VALUES(style), horse_name = VALUES(horse_name), death_time = VALUES(death_time), " +
                        "jumps = VALUES(jumps), blocks_traveled = VALUES(blocks_traveled), " +
                        "total_jumps = VALUES(total_jumps), total_blocks_traveled = VALUES(total_blocks_traveled), " +
                        "backpack_data = VALUES(backpack_data), armor_data = VALUES(armor_data)")) {
            stmt.setString(1, data.getOwnerId().toString());
            stmt.setInt(2, data.getLevel());
            stmt.setInt(3, data.getExperience());
            stmt.setString(4, data.getColor().name());
            stmt.setString(5, data.getStyle().name());
            stmt.setString(6, data.getHorseName());
            stmt.setLong(7, data.getDeathTime());
            stmt.setInt(8, data.getJumps());
            stmt.setDouble(9, data.getBlocksTraveled());
            stmt.setInt(10, data.getTotalJumps());
            stmt.setDouble(11, data.getTotalBlocksTraveled());

            if (data.getBackpackItems() != null && data.getBackpackItems().length > 0) {
                stmt.setBytes(12, serializeItemStacks(data.getBackpackItems()));
            } else {
                stmt.setNull(12, Types.BLOB);
            }

            if (data.getArmorItem() != null) {
                ItemStack[] armor = {data.getArmorItem()};
                stmt.setBytes(13, serializeItemStacks(armor));
            } else {
                stmt.setNull(13, Types.BLOB);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.warning("Error saving player data to DB: " + e.getMessage());
        }
    }

    @Override
    public void saveAllData() {
        if (connection == null) return;
        try {
            connection.setAutoCommit(false);
            saveAllHorses();
            saveAllPassengers();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                logger.severe("Transaction rollback error: " + ex.getMessage());
            }
            logger.severe("Error saving all data to DB: " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.warning("Error restoring autoCommit: " + e.getMessage());
            }
        }
    }

    private void saveAllHorses() throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO pet_horses (player_uuid, level, experience, color, style, horse_name, " +
                        "death_time, jumps, blocks_traveled, total_jumps, total_blocks_traveled, backpack_data, armor_data) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        "level = VALUES(level), experience = VALUES(experience), color = VALUES(color), " +
                        "style = VALUES(style), horse_name = VALUES(horse_name), death_time = VALUES(death_time), " +
                        "jumps = VALUES(jumps), blocks_traveled = VALUES(blocks_traveled), " +
                        "total_jumps = VALUES(total_jumps), total_blocks_traveled = VALUES(total_blocks_traveled), " +
                        "backpack_data = VALUES(backpack_data), armor_data = VALUES(armor_data)")) {
            for (HorseData data : horsesData.values()) {
                stmt.setString(1, data.getOwnerId().toString());
                stmt.setInt(2, data.getLevel());
                stmt.setInt(3, data.getExperience());
                stmt.setString(4, data.getColor().name());
                stmt.setString(5, data.getStyle().name());
                stmt.setString(6, data.getHorseName());
                stmt.setLong(7, data.getDeathTime());
                stmt.setInt(8, data.getJumps());
                stmt.setDouble(9, data.getBlocksTraveled());
                stmt.setInt(10, data.getTotalJumps());
                stmt.setDouble(11, data.getTotalBlocksTraveled());

                if (data.getBackpackItems() != null && data.getBackpackItems().length > 0) {
                    stmt.setBytes(12, serializeItemStacks(data.getBackpackItems()));
                } else {
                    stmt.setNull(12, Types.BLOB);
                }

                if (data.getArmorItem() != null) {
                    ItemStack[] armor = {data.getArmorItem()};
                    stmt.setBytes(13, serializeItemStacks(armor));
                } else {
                    stmt.setNull(13, Types.BLOB);
                }

                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    private void saveAllPassengers() throws SQLException {
        try (PreparedStatement clearStmt = connection.prepareStatement(
                "DELETE FROM pet_horse_passengers WHERE owner_uuid = ?")) {
            for (UUID ownerId : passengerPermissions.keySet()) {
                clearStmt.setString(1, ownerId.toString());
                clearStmt.addBatch();
            }
            clearStmt.executeBatch();
        }

        try (PreparedStatement insertStmt = connection.prepareStatement(
                "INSERT INTO pet_horse_passengers (owner_uuid, passenger_uuid) VALUES (?, ?)")) {
            for (Map.Entry<UUID, Set<UUID>> entry : passengerPermissions.entrySet()) {
                UUID ownerId = entry.getKey();
                for (UUID passengerId : entry.getValue()) {
                    insertStmt.setString(1, ownerId.toString());
                    insertStmt.setString(2, passengerId.toString());
                    insertStmt.addBatch();
                }
            }
            insertStmt.executeBatch();
        }
    }

    @Override
    public Set<UUID> getPassengers(UUID ownerUUID) {
        return passengerPermissions.getOrDefault(ownerUUID, Collections.emptySet());
    }

    @Override
    public void addPassenger(UUID ownerUUID, UUID passengerUUID) {
        passengerPermissions.computeIfAbsent(ownerUUID, k -> new HashSet<>()).add(passengerUUID);
        if (connection != null) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO pet_horse_passengers (owner_uuid, passenger_uuid) VALUES (?, ?)")) {
                stmt.setString(1, ownerUUID.toString());
                stmt.setString(2, passengerUUID.toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public void removePassenger(UUID ownerUUID, UUID passengerUUID) {
        passengerPermissions.getOrDefault(ownerUUID, Collections.emptySet()).remove(passengerUUID);
        if (connection != null) {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM pet_horse_passengers WHERE owner_uuid = ? AND passenger_uuid = ?")) {
                stmt.setString(1, ownerUUID.toString());
                stmt.setString(2, passengerUUID.toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
            }
        }
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.warning("Error closing DB connection: " + e.getMessage());
            }
        }
    }

    private Horse.Color parseColor(String colorStr) {
        try {
            return Horse.Color.valueOf(colorStr);
        } catch (IllegalArgumentException e) {
            return Horse.Color.BROWN;
        }
    }

    private Horse.Style parseStyle(String styleStr) {
        try {
            return Horse.Style.valueOf(styleStr);
        } catch (IllegalArgumentException e) {
            return Horse.Style.NONE;
        }
    }

    private byte[] serializeItemStacks(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.warning("Error serializing item stacks: " + e.getMessage());
            return new byte[0];
        }
    }

    private ItemStack[] deserializeItemStacks(byte[] data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            int size = dataInput.readInt();
            ItemStack[] items = new ItemStack[size];

            for (int i = 0; i < size; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (Exception e) {
            logger.warning("Error deserializing item stacks: " + e.getMessage());
            return new ItemStack[0];
        }
    }
}