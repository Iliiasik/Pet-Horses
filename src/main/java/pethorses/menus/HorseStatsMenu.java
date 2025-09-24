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
    private static final int INVENTORY_SIZE = 36;
    private static final int SLOT_LEVEL = 11;
    private static final int SLOT_SPEED = 12;
    private static final int SLOT_HEALTH = 13;
    private static final int SLOT_JUMP = 14;
    private static final int SLOT_STATUS = 15;
    private static final int SLOT_TOTAL_BLOCKS = 34;
    private static final int SLOT_TOTAL_JUMPS = 35;

    public static void open(Player player, PetHorses plugin) {
        LocalizationManager lm = plugin.getLocalizationManager();
        HorseService horseService = plugin.getHorseService();
        HorseData data = horseService.getHorseData(player.getUniqueId());

        Inventory inv = Bukkit.createInventory(null, INVENTORY_SIZE, lm.getMessage("menu.stats.title"));

        ItemStack levelItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta levelMeta = levelItem.getItemMeta();
        levelMeta.setDisplayName(lm.getMessage("menu.stats.level")
                .replace("{level}", String.valueOf(data.getLevel())));
        levelMeta.setLore(Arrays.asList(
                lm.getMessage("menu.stats.experience")
                        .replace("{current}", formatNumber(data.getExperience()))
                        .replace("{required}", formatNumber(horseService.getXpRequiredForNextLevel(data.getLevel()))),
                lm.getMessage("menu.stats.xp_to_next")
                        .replace("{remaining}", formatNumber(horseService.getXpRequiredForNextLevel(data.getLevel()) - data.getExperience()))
        ));
        levelItem.setItemMeta(levelMeta);
        inv.setItem(SLOT_LEVEL, levelItem);

        ItemStack speedItem = new ItemStack(Material.SUGAR);
        ItemMeta speedMeta = speedItem.getItemMeta();
        speedMeta.setDisplayName(lm.getMessage("menu.stats.speed")
                .replace("{value}", String.format("%.1f", 100.0 + (data.getLevel() * 5))));
        speedItem.setItemMeta(speedMeta);
        inv.setItem(SLOT_SPEED, speedItem);

        ItemStack healthItem = new ItemStack(Material.APPLE);
        ItemMeta healthMeta = healthItem.getItemMeta();
        healthMeta.setDisplayName(lm.getMessage("menu.stats.health")
                .replace("{value}", String.format("%.1f", 15 + (15 * (data.getLevel() / 20.0)))));
        healthItem.setItemMeta(healthMeta);
        inv.setItem(SLOT_HEALTH, healthItem);

        ItemStack jumpItem = new ItemStack(Material.RABBIT_FOOT);
        ItemMeta jumpMeta = jumpItem.getItemMeta();
        jumpMeta.setDisplayName(lm.getMessage("menu.stats.jump_strength")
                .replace("{value}", String.format("%.1f", 40 + (60 * (data.getLevel() / 20.0)))));
        jumpItem.setItemMeta(jumpMeta);
        inv.setItem(SLOT_JUMP, jumpItem);

        boolean isOnCooldown = horseService.isOnCooldown(player.getUniqueId());
        ItemStack statusItem = new ItemStack(isOnCooldown ? Material.RED_STAINED_GLASS_PANE : Material.LIME_STAINED_GLASS_PANE);
        ItemMeta statusMeta = statusItem.getItemMeta();
        statusMeta.setDisplayName(isOnCooldown ? lm.getMessage("menu.stats.status_cooldown") : lm.getMessage("menu.stats.status_ready"));
        if (isOnCooldown) {
            statusMeta.setLore(Arrays.asList(
                    lm.getMessage("menu.stats.cooldown_remaining")
                            .replace("{time}", horseService.getCooldownLeftFormatted(player.getUniqueId()))
            ));
        }
        statusItem.setItemMeta(statusMeta);
        inv.setItem(SLOT_STATUS, statusItem);

        ItemStack totalBlocksItem = new ItemStack(Material.FILLED_MAP);
        ItemMeta totalBlocksMeta = totalBlocksItem.getItemMeta();
        totalBlocksMeta.setDisplayName(lm.getMessage("menu.stats.total_blocks")
                .replace("{value}", formatNumber(data.getTotalBlocksTraveled())));
        totalBlocksMeta.setLore(Arrays.asList(
                lm.getMessage("menu.stats.total_blocks_description")
        ));
        totalBlocksItem.setItemMeta(totalBlocksMeta);
        inv.setItem(SLOT_TOTAL_BLOCKS, totalBlocksItem);

        ItemStack totalJumpsItem = new ItemStack(Material.FEATHER);
        ItemMeta totalJumpsMeta = totalJumpsItem.getItemMeta();
        totalJumpsMeta.setDisplayName(lm.getMessage("menu.stats.total_jumps")
                .replace("{value}", formatNumber(data.getTotalJumps())));
        totalJumpsMeta.setLore(Arrays.asList(
                lm.getMessage("menu.stats.total_jumps_description")
        ));
        totalJumpsItem.setItemMeta(totalJumpsMeta);
        inv.setItem(SLOT_TOTAL_JUMPS, totalJumpsItem);

        player.openInventory(inv);
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