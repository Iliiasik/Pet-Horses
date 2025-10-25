package pethorses.storage.database;

import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;
import pethorses.storage.HorseData;
import pethorses.util.TextUtil;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class HorseDataRepository {
    private final DatabaseManager dbManager;
    private final Logger logger;

    public HorseDataRepository(DatabaseManager dbManager, Logger logger) {
        this.dbManager = dbManager;
        this.logger = logger;
        initializeTable();
    }

    private void initializeTable() {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS pet_horses (" +
                    "player_uuid VARCHAR(36) PRIMARY KEY," +
                    "level INT NOT NULL DEFAULT 1," +
                    "experience INT NOT NULL DEFAULT 0," +
                    "color VARCHAR(20) NOT NULL DEFAULT 'BROWN'," +
                    "style VARCHAR(20) NOT NULL DEFAULT 'NONE'," +
                    "horse_name VARCHAR(32)," +
                    "horse_name_color VARCHAR(16)," +
                    "death_time BIGINT NOT NULL DEFAULT 0," +
                    "jumps INT NOT NULL DEFAULT 0," +
                    "blocks_traveled DOUBLE NOT NULL DEFAULT 0.0," +
                    "total_jumps INT NOT NULL DEFAULT 0," +
                    "total_blocks_traveled DOUBLE NOT NULL DEFAULT 0.0," +
                    "backpack_data LONGBLOB," +
                    "armor_data LONGBLOB" +
                    ")");
        } catch (SQLException e) {
            logger.severe("Error initializing horse table: " + e.getMessage());
        }
    }

    public Map<UUID, HorseData> loadAll() {
        Map<UUID, HorseData> horsesData = new HashMap<>();
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM pet_horses")) {
            while (rs.next()) {
                UUID playerId = UUID.fromString(rs.getString("player_uuid"));
                HorseData data = new HorseData();
                data.setOwnerId(playerId);
                data.setLevel(rs.getInt("level"));
                data.setExperience(rs.getInt("experience"));
                data.setColor(Horse.Color.valueOf(rs.getString("color")));
                data.setStyle(Horse.Style.valueOf(rs.getString("style")));
                data.setHorseName(rs.getString("horse_name"));
                String colorStr = rs.getString("horse_name_color");
                data.setHorseNameColor(TextUtil.parseNamedTextColor(colorStr));
                data.setDeathTime(rs.getLong("death_time"));
                data.setJumps(rs.getInt("jumps"));
                data.setBlocksTraveled(rs.getDouble("blocks_traveled"));
                data.setTotalJumps(rs.getInt("total_jumps"));
                data.setTotalBlocksTraveled(rs.getDouble("total_blocks_traveled"));

                byte[] backpackData = rs.getBytes("backpack_data");
                if (backpackData != null && backpackData.length > 0) {
                    data.setBackpackItems(ItemStackSerializer.deserialize(backpackData, logger));
                }

                byte[] armorData = rs.getBytes("armor_data");
                if (armorData != null && armorData.length > 0) {
                    ItemStack[] armor = ItemStackSerializer.deserialize(armorData, logger);
                    if (armor.length > 0) data.setArmorItem(armor[0]);
                }

                horsesData.put(playerId, data);
            }
        } catch (SQLException e) {
            logger.severe("Error loading horse data: " + e.getMessage());
        }
        return horsesData;
    }

    public void save(HorseData data) {
        if (data == null || data.getOwnerId() == null) return;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO pet_horses (player_uuid, level, experience, color, style, horse_name, horse_name_color, " +
                             "death_time, jumps, blocks_traveled, total_jumps, total_blocks_traveled, backpack_data, armor_data) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE " +
                             "level = VALUES(level), experience = VALUES(experience), color = VALUES(color), " +
                             "style = VALUES(style), horse_name = VALUES(horse_name), horse_name_color = VALUES(horse_name_color), death_time = VALUES(death_time), " +
                             "jumps = VALUES(jumps), blocks_traveled = VALUES(blocks_traveled), " +
                             "total_jumps = VALUES(total_jumps), total_blocks_traveled = VALUES(total_blocks_traveled), " +
                             "backpack_data = VALUES(backpack_data), armor_data = VALUES(armor_data)")) {
            stmt.setString(1, data.getOwnerId().toString());
            stmt.setInt(2, data.getLevel());
            stmt.setInt(3, data.getExperience());
            stmt.setString(4, (data.getColor() != null) ? data.getColor().name() : Horse.Color.BROWN.name());
            stmt.setString(5, (data.getStyle() != null) ? data.getStyle().name() : Horse.Style.NONE.name());
            stmt.setString(6, data.getHorseName());
            stmt.setString(7, TextUtil.namedTextColorToKey(data.getHorseNameColor()));
            stmt.setLong(8, data.getDeathTime());
            stmt.setInt(9, data.getJumps());
            stmt.setDouble(10, data.getBlocksTraveled());
            stmt.setInt(11, data.getTotalJumps());
            stmt.setDouble(12, data.getTotalBlocksTraveled());
            stmt.setBytes(13, ItemStackSerializer.serialize(data.getBackpackItems(), logger));
            stmt.setBytes(14, ItemStackSerializer.serialize(new ItemStack[]{data.getArmorItem()}, logger));
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.warning("Error saving horse data: " + e.getMessage());
        }
    }
}
