package nomadhorses.config;

import nomadhorses.NomadHorses;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final NomadHorses plugin;
    private FileConfiguration config;

    public ConfigManager(NomadHorses plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.config.options().copyDefaults(true);
        plugin.saveConfig();
    }

    public String getLanguage() {
        return config.getString("language", "en");
    }

    public boolean isDatabaseEnabled() {
        return config.getBoolean("database.enabled", false);
    }

    public String getDatabaseHost() {
        return config.getString("database.host", "localhost");
    }

    public int getDatabasePort() {
        return config.getInt("database.port", 3306);
    }

    public String getDatabaseName() {
        return config.getString("database.name", "minecraft");
    }

    public String getDatabaseUser() {
        return config.getString("database.user", "root");
    }

    public String getDatabasePassword() {
        return config.getString("database.password", "");
    }

    public int getBaseXpForLevel() {
        return config.getInt("leveling.base_xp", 100);
    }

    public int getXpIncrementPerLevel() {
        return config.getInt("leveling.xp_increment", 50);
    }

    public double getSpeedBase() {
        return config.getDouble("stats.speed_base", 0.18);
    }

    public double getSpeedMaxBonus() {
        return config.getDouble("stats.speed_max_bonus", 0.225);
    }

    public double getHealthBase() {
        return config.getDouble("stats.health_base", 15.0);
    }

    public double getHealthMaxBonus() {
        return config.getDouble("stats.health_max_bonus", 15.0);
    }

    public double getJumpBase() {
        return config.getDouble("stats.jump_base", 0.6);
    }

    public double getJumpMaxBonus() {
        return config.getDouble("stats.jump_max_bonus", 0.6);
    }

    public long getRespawnCooldownMinutes() {
        return config.getLong("respawn_cooldown_minutes", 15);
    }

    public int getBackpackBaseSize() {
        return config.getInt("backpack.base_size", 9);
    }

    public int getBackpackSizePerLevel() {
        return config.getInt("backpack.size_per_level", 9);
    }

    public int getBackpackMaxSize() {
        return config.getInt("backpack.max_size", 54);
    }

    public boolean isArmorSlotEnabled() {
        return config.getBoolean("backpack.armor_slot_enabled", true);
    }
}