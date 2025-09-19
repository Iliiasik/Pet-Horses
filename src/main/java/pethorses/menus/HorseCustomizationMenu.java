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

    public HorseCustomizationMenu(PetHorses plugin) {
        this.plugin = plugin;
        this.horseService = plugin.getHorseService();
        this.localizationManager = plugin.getLocalizationManager();
    }

    public static void open(Player player, PetHorses plugin) {
        HorseCustomizationMenu instance = new HorseCustomizationMenu(plugin);
        LocalizationManager lm = plugin.getLocalizationManager();
        HorseService hs = plugin.getHorseService();
        Inventory inv = plugin.getServer().createInventory(null, INVENTORY_SIZE, lm.getMessage("menu.customize.title"));
        HorseData data = hs.getHorseData(player.getUniqueId());

        setColorItem(inv, 10, Horse.Color.WHITE, Material.WHITE_WOOL, lm.getMessage("menu.customize.color_white"));
        setColorItem(inv, 11, Horse.Color.CREAMY, Material.BONE_MEAL, lm.getMessage("menu.customize.color_creamy"));
        setColorItem(inv, 12, Horse.Color.CHESTNUT, Material.ACACIA_PLANKS, lm.getMessage("menu.customize.color_chestnut"));
        setColorItem(inv, 13, Horse.Color.BROWN, Material.SPRUCE_PLANKS, lm.getMessage("menu.customize.color_brown"));
        setColorItem(inv, 14, Horse.Color.BLACK, Material.BLACK_WOOL, lm.getMessage("menu.customize.color_black"));
        setColorItem(inv, 15, Horse.Color.GRAY, Material.LIGHT_GRAY_WOOL, lm.getMessage("menu.customize.color_gray"));
        setColorItem(inv, 16, Horse.Color.DARK_BROWN, Material.DARK_OAK_PLANKS, lm.getMessage("menu.customize.color_dark_brown"));

        ItemStack nameItem = new ItemStack(Material.NAME_TAG);
        ItemMeta nameMeta = nameItem.getItemMeta();
        nameMeta.setDisplayName(lm.getMessage("menu.customize.name_item"));
        nameMeta.setLore(Arrays.asList(
                lm.getMessage("menu.customize.current_name")
                        .replace("{name}", data.getHorseName() == null ? lm.getMessage("menu.customize.no_name") : data.getHorseName()),
                lm.getMessage("menu.customize.name_prompt_1"),
                lm.getMessage("menu.customize.name_prompt_2")
        ));
        nameItem.setItemMeta(nameMeta);
        inv.setItem(22, nameItem);

        ItemStack saveItem = new ItemStack(Material.EMERALD);
        ItemMeta saveMeta = saveItem.getItemMeta();
        saveMeta.setDisplayName(lm.getMessage("menu.customize.save_item"));
        saveItem.setItemMeta(saveMeta);
        inv.setItem(26, saveItem);

        player.openInventory(inv);
    }

    private static void setColorItem(Inventory inv, int slot, Horse.Color color, Material icon, String colorName) {
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Color: " + ChatColor.YELLOW + colorName);
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Click to select"));
        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(localizationManager.getMessage("menu.customize.title"))) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

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
            case NAME_TAG:
                player.closeInventory();
                player.sendMessage(localizationManager.getMessage("menu.customize.enter_name_prompt"));
                plugin.getChatInputListener().awaitInput(player, name -> {
                    data.setHorseName(name);
                    player.sendMessage(localizationManager.getMessage("menu.customize.name_changed")
                            .replace("{name}", name));
                    open(player, plugin);
                });
                return;
            case EMERALD:
                if (event.getSlot() == 26) {
                    player.closeInventory();
                    player.sendMessage(localizationManager.getMessage("menu.customize.saved"));
                    if (data.getHorseId() != null) {
                        Entity horse = plugin.getServer().getEntity(data.getHorseId());
                        if (horse instanceof Horse h) {
                            h.setColor(data.getColor());
                            h.setStyle(data.getStyle());
                            if (data.getHorseName() != null) {
                                h.setCustomName(data.getHorseName());
                                h.setCustomNameVisible(true);
                            }
                        }
                    }
                    return;
                }
                break;
        }

        if (updated) {
            player.sendMessage(localizationManager.getMessage("menu.customize.color_changed"));
            open(player, plugin);
        }
    }
}