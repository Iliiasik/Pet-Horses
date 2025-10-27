package pethorses.listeners;

import pethorses.PetHorses;
import pethorses.services.HorseService;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;

public class HorseArmorListener implements Listener {
    private final HorseService horseService;

    public HorseArmorListener(PetHorses plugin) {
        this.horseService = plugin.getHorseService();
    }

    @EventHandler
    public void onHorseInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory() instanceof HorseInventory horseInv)) return;

        if (horseInv.getHolder() instanceof Horse horse) {
            if (horse.isTamed() && horse.getOwner() != null &&
                    horse.getOwner().getUniqueId().equals(player.getUniqueId())) {

                ItemStack armor = horseInv.getArmor();
                horseService.saveHorseArmor(player.getUniqueId(), armor);

                ItemStack current = event.getCurrentItem();

                if (current != null && current.getType() != Material.AIR) {
                    boolean isSaddle = current.getType() == Material.SADDLE;

                    if (isSaddle) {
                        if (event.isShiftClick()) {
                            event.setCancelled(true);
                        } else {
                            ItemStack cursor = event.getCursor();
                            if (cursor.getType().isAir()) {
                                event.setCancelled(true);
                            } else if (event.getHotbarButton() >= 0) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getInventory() instanceof HorseInventory)) return;
        for (int slot : event.getRawSlots()) {
            if (slot >= event.getInventory().getSize()) continue;
            ItemStack current = event.getInventory().getItem(slot);
            if (current == null || current.getType() == Material.AIR) continue;
            if (current.getType() == Material.SADDLE) {
                event.setCancelled(true);
                return;
            }
        }
    }
}