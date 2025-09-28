package pethorses.menus;

import pethorses.PetHorses;
import pethorses.config.LocalizationManager;
import pethorses.services.HorseService;
import pethorses.storage.HorseData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class HorseStatsMenu {
    private static final int INVENTORY_SIZE = 27;
    private static final int SLOT_LEVEL = 10;
    private static final int SLOT_SPEED = 11;
    private static final int SLOT_HEALTH = 12;
    private static final int SLOT_JUMP = 13;
    private static final int SLOT_STATUS = 14;
    private static final int SLOT_TOTAL_BLOCKS = 16;
    private static final int SLOT_TOTAL_JUMPS = 17;

    public static void open(Player player, PetHorses plugin) {
        LocalizationManager lm = plugin.getLocalizationManager();
        HorseService horseService = plugin.getHorseService();
        HorseData data = horseService.getHorseData(player.getUniqueId());

        Inventory inv = Bukkit.createInventory(null, INVENTORY_SIZE, lm.getMessage("menu.stats.title"));

        setMenuItem(inv, SLOT_LEVEL, Material.EXPERIENCE_BOTTLE, lm.getMessage("menu.stats.level")
                        .replace("{level}", String.valueOf(data.getLevel())),
                Arrays.asList(
                        lm.getMessage("menu.stats.experience")
                                .replace("{current}", formatNumber(data.getExperience()))
                                .replace("{required}", formatNumber(horseService.getXpRequiredForNextLevel(data.getLevel()))),
                        lm.getMessage("menu.stats.xp_to_next")
                                .replace("{remaining}", formatNumber(horseService.getXpRequiredForNextLevel(data.getLevel()) - data.getExperience()))
                ));
        setMenuItem(inv, SLOT_SPEED, Material.SUGAR, lm.getMessage("menu.stats.speed")
                .replace("{value}", String.format("%.1f", 100.0 + (data.getLevel() * 5))), null);
        setMenuItem(inv, SLOT_HEALTH, Material.APPLE, lm.getMessage("menu.stats.health")
                .replace("{value}", String.format("%.1f", 15 + (15 * (data.getLevel() / 20.0)))), null);
        setMenuItem(inv, SLOT_JUMP, Material.RABBIT_FOOT, lm.getMessage("menu.stats.jump_strength")
                .replace("{value}", String.format("%.1f", 40 + (60 * (data.getLevel() / 20.0)))), null);

        boolean isOnCooldown = horseService.isOnCooldown(player.getUniqueId());
        setMenuItem(inv, SLOT_STATUS, isOnCooldown ? Material.REDSTONE : Material.LIME_DYE,
                isOnCooldown ? lm.getMessage("menu.stats.status_cooldown") : lm.getMessage("menu.stats.status_ready"),
                isOnCooldown ? Arrays.asList(
                        lm.getMessage("menu.stats.cooldown_remaining")
                                .replace("{time}", horseService.getCooldownLeftFormatted(player.getUniqueId()))
                ) : null);

        setMenuItem(inv, SLOT_TOTAL_BLOCKS, Material.FILLED_MAP, lm.getMessage("menu.stats.total_blocks")
                        .replace("{value}", formatNumber(data.getTotalBlocksTraveled())),
                Arrays.asList(
                        lm.getMessage("menu.stats.total_blocks_description")
                ));
        setMenuItem(inv, SLOT_TOTAL_JUMPS, Material.FEATHER, lm.getMessage("menu.stats.total_jumps")
                        .replace("{value}", formatNumber(data.getTotalJumps())),
                Arrays.asList(
                        lm.getMessage("menu.stats.total_jumps_description")
                ));

        player.openInventory(inv);
    }

    private static void setMenuItem(Inventory inv, int slot, Material material, String displayName, java.util.List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    private static String formatNumber(double number) {
        if (number < 1000) {
            return String.format("%.0f", number);
        } else if (number < 1000000) {
            return String.format("%.1fк", number / 1000).replace(".0", "");
        } else {
            return String.format("%.1fкк", number / 1000000).replace(".0", "");
        }
    }

    private static String formatNumber(int number) {
        return formatNumber((double) number);
    }
}