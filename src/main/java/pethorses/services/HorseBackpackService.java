package pethorses.services;

import pethorses.PetHorses;
import pethorses.config.ConfigManager;
import pethorses.storage.HorseData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class HorseBackpackService {
    private final PetHorses plugin;
    private final ConfigManager configManager;

    public HorseBackpackService(PetHorses plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    public void openBackpack(Player player, Horse horse) {
        if (!horse.isTamed() || horse.getOwner() == null || !horse.getOwner().getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(plugin.getLocalizationManager().getMessage("horse.not_yours"));
            return;
        }

        HorseData data = plugin.getHorseService().getHorseData(player.getUniqueId());
        int backpackSize = data.getBackpackSize(
                configManager.getBackpackBaseSize(),
                configManager.getBackpackSizePerLevel(),
                configManager.getBackpackMaxSize()
        );

        Inventory backpack = Bukkit.createInventory(null, backpackSize,
                plugin.getLocalizationManager().getMessage("menu.backpack.title"));

        if (data.getBackpackItems() != null && data.getBackpackItems().length > 0) {
            for (int i = 0; i < Math.min(data.getBackpackItems().length, backpackSize); i++) {
                backpack.setItem(i, data.getBackpackItems()[i]);
            }
        }

        player.openInventory(backpack);
    }

    public void saveBackpack(UUID playerId, Inventory inventory) {
        HorseData data = plugin.getHorseService().getHorseData(playerId);
        ItemStack[] items = new ItemStack[inventory.getSize()];

        for (int i = 0; i < inventory.getSize(); i++) {
            items[i] = inventory.getItem(i);
        }

        data.setBackpackItems(items);
        plugin.getHorseDataManager().saveHorseData(data);
    }

    public void handleArmorForHorse(Horse horse, HorseData data) {
        if (configManager.isArmorSlotEnabled() && data.getArmorItem() != null) {
            horse.getInventory().setArmor(data.getArmorItem());
        }
    }

    public void saveHorseArmor(UUID playerId, ItemStack armor) {
        if (configManager.isArmorSlotEnabled()) {
            HorseData data = plugin.getHorseService().getHorseData(playerId);
            data.setArmorItem(armor);
            plugin.getHorseDataManager().saveHorseData(data);
        }
    }

    public void saveHorseArmorFromEntity(UUID playerId, Horse horse) {
        if (configManager.isArmorSlotEnabled() && horse != null) {
            HorseData data = plugin.getHorseService().getHorseData(playerId);
            data.setArmorItem(horse.getInventory().getArmor());
            plugin.getHorseDataManager().saveHorseData(data);
        }
    }
}