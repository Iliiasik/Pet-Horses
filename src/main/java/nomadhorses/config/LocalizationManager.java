package nomadhorses.config;

import nomadhorses.NomadHorses;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class LocalizationManager {
    private final NomadHorses plugin;
    private FileConfiguration messages;

    public LocalizationManager(NomadHorses plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    private void loadMessages() {
        ConfigManager configManager = new ConfigManager(plugin);
        String lang = configManager.getLanguage();
        String fileName = "messages_" + lang + ".yml";
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }

        this.messages = YamlConfiguration.loadConfiguration(file);

        InputStream defaultStream = plugin.getResource(fileName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new java.io.InputStreamReader(defaultStream));
            this.messages.setDefaults(defaultConfig);
        }
    }

    public String getMessage(String key) {
        String message = messages.getString(key, "&cMissing translation: " + key);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}