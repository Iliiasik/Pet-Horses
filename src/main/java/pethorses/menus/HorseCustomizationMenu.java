package pethorses.menus;

import pethorses.PetHorses;
import pethorses.config.LocalizationManager;
import pethorses.storage.HorseData;
import pethorses.services.HorseService;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class HorseCustomizationMenu implements Listener {
    private static final int INVENTORY_SIZE = 27;
    private final PetHorses plugin;
    private final HorseService horseService;
    private final LocalizationManager localizationManager;

    private static final Material[] horseColorsMaterials = {
            Material.WHITE_WOOL, Material.BONE_MEAL, Material.ACACIA_PLANKS,
            Material.SPRUCE_PLANKS, Material.BLACK_WOOL, Material.LIGHT_GRAY_WOOL, Material.DARK_OAK_PLANKS
    };
    private static final String[] horseColorsKeys = {
            "color_white", "color_creamy", "color_chestnut",
            "color_brown", "color_black", "color_gray", "color_dark_brown"
    };

    private static final int[] nameColorSlots = {2,3,4,5,6,11,12,13,14,15,20,21,22,23,24};

    private static final Material[] nameColorMaterials = {
            Material.WHITE_DYE,
            Material.LIGHT_GRAY_DYE,
            Material.GRAY_DYE,
            Material.BLACK_DYE,
            Material.BLUE_DYE,
            Material.LIGHT_BLUE_DYE,
            Material.GREEN_DYE,
            Material.LIME_DYE,
            Material.CYAN_DYE,
            Material.RED_DYE,
            Material.ORANGE_DYE,
            Material.YELLOW_DYE,
            Material.BROWN_DYE,
            Material.PURPLE_DYE,
            Material.PINK_DYE
    };

    private static final String[] nameColorKeys = {
            "name_color_white",
            "name_color_gray",
            "name_color_dark_gray",
            "name_color_black",
            "name_color_dark_blue",
            "name_color_blue",
            "name_color_dark_green",
            "name_color_green",
            "name_color_aqua",
            "name_color_red",
            "name_color_gold",
            "name_color_yellow",
            "name_color_brown",
            "name_color_purple",
            "name_color_light_purple"
    };

    private static final ChatColor[] nameChatColors = {
            ChatColor.WHITE,
            ChatColor.GRAY,
            ChatColor.DARK_GRAY,
            ChatColor.BLACK,
            ChatColor.DARK_BLUE,
            ChatColor.BLUE,
            ChatColor.DARK_GREEN,
            ChatColor.GREEN,
            ChatColor.AQUA,
            ChatColor.DARK_RED,
            ChatColor.GOLD,
            ChatColor.YELLOW,
            ChatColor.GOLD,
            ChatColor.DARK_PURPLE,
            ChatColor.LIGHT_PURPLE
    };

    public HorseCustomizationMenu(PetHorses plugin) {
        this.plugin = plugin;
        this.horseService = plugin.getHorseService();
        this.localizationManager = plugin.getLocalizationManager();
    }

    public static void open(Player player, PetHorses plugin) {
        LocalizationManager lm = plugin.getLocalizationManager();
        HorseService hs = plugin.getHorseService();
        Inventory inv = plugin.getServer().createInventory(null, INVENTORY_SIZE, ChatColor.DARK_AQUA + lm.getMessage("menu.customize.title"));
        HorseData data = hs.getHorseData(player.getUniqueId());

        setMenuItem(inv, 10, Material.LEATHER, ChatColor.GOLD + lm.getMessage("menu.customize.color_menu_title"), null);
        setMenuItem(inv, 12, Material.PAPER, ChatColor.AQUA + lm.getMessage("menu.customize.name_color_menu_title"), null);
        setMenuItem(inv, 14, Material.NAME_TAG, ChatColor.LIGHT_PURPLE + lm.getMessage("menu.customize.name_item"), Arrays.asList(
                ChatColor.YELLOW + lm.getMessage("menu.customize.current_name").replace("{name}", data.getHorseName() == null ? lm.getMessage("menu.customize.no_name") : data.getHorseName()),
                ChatColor.GRAY + lm.getMessage("menu.customize.name_prompt_1"),
                ChatColor.GRAY + lm.getMessage("menu.customize.name_prompt_2")
        ));
        setMenuItem(inv, 16, Material.EMERALD, ChatColor.GREEN + lm.getMessage("menu.customize.save_item"), null);

        player.openInventory(inv);
    }

    public static void openColorMenu(Player player, PetHorses plugin) {
        LocalizationManager lm = plugin.getLocalizationManager();
        Inventory inv = plugin.getServer().createInventory(null, 27, ChatColor.GOLD + lm.getMessage("menu.customize.color_menu_title"));
        for (int i = 0; i < horseColorsMaterials.length; i++) {
            setMenuItem(inv, 10 + i, horseColorsMaterials[i], lm.getMessage("menu.customize." + horseColorsKeys[i]), null);
        }
        player.openInventory(inv);
    }

    public static void openNameColorMenu(Player player, PetHorses plugin) {
        LocalizationManager lm = plugin.getLocalizationManager();
        Inventory inv = plugin.getServer().createInventory(null, 27, ChatColor.AQUA + lm.getMessage("menu.customize.name_color_menu_title"));
        for (int i = 0; i < nameColorMaterials.length; i++) {
            ChatColor color = (nameColorMaterials[i] == Material.BLACK_DYE) ? ChatColor.GRAY : nameChatColors[i];
            setMenuItem(inv, nameColorSlots[i], nameColorMaterials[i], color + lm.getMessage("menu.customize." + nameColorKeys[i]), null);
        }
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(ChatColor.DARK_AQUA + localizationManager.getMessage("menu.customize.title"))) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) return;
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null || clicked.getType() == Material.AIR) return;
            if (clicked.getType() == Material.LEATHER && event.getSlot() == 10) {
                openColorMenu(player, plugin);
                return;
            }
            if (clicked.getType() == Material.PAPER && event.getSlot() == 12) {
                openNameColorMenu(player, plugin);
                return;
            }
            HorseData data = horseService.getHorseData(player.getUniqueId());
            if (clicked.getType() == Material.NAME_TAG && event.getSlot() == 14) {
                player.closeInventory();
                player.sendMessage(localizationManager.getMessage("menu.customize.enter_name_prompt"));
                plugin.getChatInputListener().awaitInput(player, name -> {
                    data.setHorseName(name);
                    player.sendMessage(localizationManager.getMessage("menu.customize.name_changed").replace("{name}", name));
                    open(player, plugin);
                });
                return;
            }
            if (clicked.getType() == Material.EMERALD && event.getSlot() == 16) {
                player.closeInventory();
                player.sendMessage(localizationManager.getMessage("menu.customize.saved"));
                if (data.getHorseId() != null) {
                    Entity horse = plugin.getServer().getEntity(data.getHorseId());
                    if (horse instanceof Horse h) {
                        h.setColor(data.getColor());
                        h.setStyle(data.getStyle());
                        if (data.getHorseName() != null) {
                            h.setCustomName(data.getHorseNameColor() + data.getHorseName());
                            h.setCustomNameVisible(true);
                        }
                    }
                }
                return;
            }
        }
        if (title.equals(ChatColor.GOLD + localizationManager.getMessage("menu.customize.color_menu_title"))) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) return;
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null) return;
            HorseData data = horseService.getHorseData(player.getUniqueId());
            boolean updated = false;
            switch (clicked.getType()) {
                case WHITE_WOOL:
                    data.setColor(Horse.Color.WHITE);
                    updated = true;
                    break;
                case BONE_MEAL:
                    data.setColor(Horse.Color.CREAMY);
                    updated = true;
                    break;
                case ACACIA_PLANKS:
                    data.setColor(Horse.Color.CHESTNUT);
                    updated = true;
                    break;
                case SPRUCE_PLANKS:
                    data.setColor(Horse.Color.BROWN);
                    updated = true;
                    break;
                case BLACK_WOOL:
                    data.setColor(Horse.Color.BLACK);
                    updated = true;
                    break;
                case LIGHT_GRAY_WOOL:
                    data.setColor(Horse.Color.GRAY);
                    updated = true;
                    break;
                case DARK_OAK_PLANKS:
                    data.setColor(Horse.Color.DARK_BROWN);
                    updated = true;
                    break;
            }
            if (updated) {
                player.sendMessage(localizationManager.getMessage("menu.customize.color_changed"));
                open(player, plugin);
            }
        }
        if (title.equals(ChatColor.AQUA + localizationManager.getMessage("menu.customize.name_color_menu_title"))) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player player)) return;
            ItemStack clicked = event.getCurrentItem();
            if (clicked == null) return;
            HorseData data = horseService.getHorseData(player.getUniqueId());
            for (int i = 0; i < nameColorMaterials.length; i++) {
                if (clicked.getType() == nameColorMaterials[i]) {
                    data.setHorseNameColor(nameChatColors[i]);
                    break;
                }
            }
            open(player, plugin);
        }
    }
}