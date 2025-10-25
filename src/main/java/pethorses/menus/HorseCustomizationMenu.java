package pethorses.menus;

import pethorses.PetHorses;
import pethorses.config.LocalizationManager;
import pethorses.inventory.MenuHolder;
import pethorses.storage.HorseData;
import pethorses.services.HorseService;
import pethorses.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import org.bukkit.scheduler.BukkitRunnable;

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

    private static final NamedTextColor[] nameNamedColors = {
            NamedTextColor.WHITE,
            NamedTextColor.GRAY,
            NamedTextColor.DARK_GRAY,
            NamedTextColor.BLACK,
            NamedTextColor.DARK_BLUE,
            NamedTextColor.BLUE,
            NamedTextColor.DARK_GREEN,
            NamedTextColor.GREEN,
            NamedTextColor.AQUA,
            NamedTextColor.RED,
            NamedTextColor.GOLD,
            NamedTextColor.YELLOW,
            NamedTextColor.GOLD,
            NamedTextColor.DARK_PURPLE,
            NamedTextColor.LIGHT_PURPLE
    };

    public HorseCustomizationMenu(PetHorses plugin) {
        this.plugin = plugin;
        this.horseService = plugin.getHorseService();
        this.localizationManager = plugin.getLocalizationManager();
    }

    public static void open(Player player, PetHorses plugin) {
        LocalizationManager lm = plugin.getLocalizationManager();
        HorseService hs = plugin.getHorseService();
        MenuHolder holder = new MenuHolder("customize_main");
        Inventory inv = plugin.getServer().createInventory(holder, INVENTORY_SIZE, Component.text(lm.getMessage("menu.customize.title")));
        HorseData data = hs.getHorseData(player.getUniqueId());

        setMenuItem(inv, 10, Material.LEATHER, lm.getMessage("menu.customize.color_menu_title"), null);
        setMenuItem(inv, 12, Material.PAPER, lm.getMessage("menu.customize.name_color_menu_title"), null);
        setMenuItem(inv, 14, Material.NAME_TAG, lm.getMessage("menu.customize.name_item"), Arrays.asList(
                lm.getMessage("menu.customize.current_name").replace("{name}", data.getHorseName() == null ? lm.getMessage("menu.customize.no_name") : data.getHorseName()),
                lm.getMessage("menu.customize.name_prompt_1"),
                lm.getMessage("menu.customize.name_prompt_2")
        ));
        setMenuItem(inv, 16, Material.EMERALD, lm.getMessage("menu.customize.save_item"), null);

        player.openInventory(inv);
    }

    public static void openColorMenu(Player player, PetHorses plugin) {
        LocalizationManager lm = plugin.getLocalizationManager();
        MenuHolder holder = new MenuHolder("customize_color");
        Inventory inv = plugin.getServer().createInventory(holder, 27, Component.text(lm.getMessage("menu.customize.color_menu_title")));
        for (int i = 0; i < horseColorsMaterials.length; i++) {
            setMenuItem(inv, 10 + i, horseColorsMaterials[i], lm.getMessage("menu.customize." + horseColorsKeys[i]), null);
        }
        player.openInventory(inv);
    }

    public static void openNameColorMenu(Player player, PetHorses plugin) {
        LocalizationManager lm = plugin.getLocalizationManager();
        MenuHolder holder = new MenuHolder("customize_name_color");
        Inventory inv = plugin.getServer().createInventory(holder, 27, Component.text(lm.getMessage("menu.customize.name_color_menu_title")));
        for (int i = 0; i < nameColorMaterials.length; i++) {
            setMenuItem(inv, nameColorSlots[i], nameColorMaterials[i], lm.getMessage("menu.customize." + nameColorKeys[i]), null);
        }
        player.openInventory(inv);
    }

    private static void setMenuItem(Inventory inv, int slot, Material material, String displayName, java.util.List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName));
            if (lore != null) {
                java.util.List<Component> lc = new java.util.ArrayList<>();
                for (String s : lore) lc.add(Component.text(s));
                meta.lore(lc);
            }
            item.setItemMeta(meta);
        }
        inv.setItem(slot, item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!(event.getInventory().getHolder() instanceof MenuHolder holder)) return;
        String id = holder.getId();
        if ("customize_main".equals(id)) {
            handleMainMenuClick(event);
        } else if ("customize_color".equals(id)) {
            handleColorMenuClick(event);
        } else if ("customize_name_color".equals(id)) {
            handleNameColorMenuClick(event);
        }
    }

    private void handleMainMenuClick(InventoryClickEvent event) {
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
            event.setCancelled(true);
            player.closeInventory();
            player.sendMessage(localizationManager.getMessage("menu.customize.saved"));
            data.setOwnerId(player.getUniqueId());
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getHorseDataManager().saveHorseData(data);
                }
            }.runTask(plugin);

            if (data.getHorseId() != null) {
                Entity horse = plugin.getServer().getEntity(data.getHorseId());
                if (horse instanceof Horse h) {
                    h.setColor(data.getColor());
                    h.setStyle(data.getStyle());
                    if (data.getHorseName() != null) {
                        h.customName(TextUtil.colored(data.getHorseNameColor(), data.getHorseName()));
                        h.setCustomNameVisible(true);
                    }
                }
            }
        }
    }

    private void handleColorMenuClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        HorseData data = horseService.getHorseData(player.getUniqueId());
        boolean updated = false;
        switch (clicked.getType()) {
            case WHITE_WOOL -> {
                data.setColor(Horse.Color.WHITE);
                updated = true;
            }
            case BONE_MEAL -> {
                data.setColor(Horse.Color.CREAMY);
                updated = true;
            }
            case ACACIA_PLANKS -> {
                data.setColor(Horse.Color.CHESTNUT);
                updated = true;
            }
            case SPRUCE_PLANKS -> {
                data.setColor(Horse.Color.BROWN);
                updated = true;
            }
            case BLACK_WOOL -> {
                data.setColor(Horse.Color.BLACK);
                updated = true;
            }
            case LIGHT_GRAY_WOOL -> {
                data.setColor(Horse.Color.GRAY);
                updated = true;
            }
            case DARK_OAK_PLANKS -> {
                data.setColor(Horse.Color.DARK_BROWN);
                updated = true;
            }
        }
        if (updated) {
            player.sendMessage(localizationManager.getMessage("menu.customize.color_changed"));
            open(player, plugin);
        }
    }

    private void handleNameColorMenuClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;
        HorseData data = plugin.getHorseService().getHorseData(player.getUniqueId());
        for (int i = 0; i < nameColorMaterials.length; i++) {
            if (clicked.getType() == nameColorMaterials[i]) {
                data.setHorseNameColor(nameNamedColors[i]);
                break;
            }
        }
        open(player, plugin);
    }
}
