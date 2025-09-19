package pethorses;

import pethorses.commands.HorseCommandExecutor;
import pethorses.config.ConfigManager;
import pethorses.config.LocalizationManager;
import pethorses.listeners.*;
import pethorses.menus.HorseCustomizationMenu;
import pethorses.services.HorseBackpackService;
import pethorses.services.HorseService;
import pethorses.services.PassengerService;
import pethorses.storage.HorseDataManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PetHorses extends JavaPlugin {
    private ConfigManager configManager;
    private LocalizationManager localizationManager;
    private HorseDataManager horseDataManager;
    private HorseService horseService;
    private PassengerService passengerService;
    private ChatInputListener chatInputListener;
    private HorseBackpackService horseBackpackService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        this.configManager = new ConfigManager(this);
        this.localizationManager = new LocalizationManager(this);
        this.horseDataManager = new HorseDataManager(this);
        this.horseService = new HorseService(this);
        this.passengerService = new PassengerService(this);
        this.chatInputListener = new ChatInputListener(this);
        this.horseBackpackService = new HorseBackpackService(this);

        getCommand("horse").setExecutor(new HorseCommandExecutor(this));

        getServer().getPluginManager().registerEvents(new HorseEventListener(this), this);
        getServer().getPluginManager().registerEvents(new HorseMovementListener(this), this);
        getServer().getPluginManager().registerEvents(new HorseStatsMenuListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuClickListener(this), this);
        getServer().getPluginManager().registerEvents(new HorseCustomizationMenu(this), this);
        getServer().getPluginManager().registerEvents(chatInputListener, this);
        getServer().getPluginManager().registerEvents(new HorseBackpackListener(this), this);
        getServer().getPluginManager().registerEvents(new BackpackInventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new HorseArmorListener(this), this);

        startAutoSaveTask();
        getLogger().info(localizationManager.getMessage("plugin.enabled"));
    }

    @Override
    public void onDisable() {
        horseDataManager.saveAllData();
        horseDataManager.close();
        getLogger().info(localizationManager.getMessage("plugin.disabled"));
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public LocalizationManager getLocalizationManager() {
        return localizationManager;
    }

    public HorseDataManager getHorseDataManager() {
        return horseDataManager;
    }

    public HorseService getHorseService() {
        return horseService;
    }

    public PassengerService getPassengerService() {
        return passengerService;
    }

    public ChatInputListener getChatInputListener() {
        return chatInputListener;
    }

    public HorseBackpackService getHorseBackpackService() {
        return horseBackpackService;
    }

    private void startAutoSaveTask() {
        long interval = 20L * 60L * 10L;
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            horseDataManager.saveAllData();
            getLogger().info(localizationManager.getMessage("plugin.autosave"));
        }, interval, interval);
    }
}