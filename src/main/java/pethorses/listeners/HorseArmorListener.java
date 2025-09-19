package pethorses.listeners;

import pethorses.PetHorses;
import pethorses.services.HorseService;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;

public class HorseArmorListener implements Listener {
    private final PetHorses plugin;
    private final HorseService horseService;

    public HorseArmorListener(PetHorses plugin) {
        this.plugin = plugin;
        this.horseService = plugin.getHorseService();
    }

    @EventHandler
    public void onHorseInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!(event.getInventory() instanceof HorseInventory)) return;

        HorseInventory horseInv = (HorseInventory) event.getInventory();
        if (horseInv.getHolder() instanceof Horse horse) {
            if (horse.isTamed() && horse.getOwner() != null &&
                    horse.getOwner().getUniqueId().equals(player.getUniqueId())) {

                ItemStack armor = horseInv.getArmor();
                horseService.saveHorseArmor(player.getUniqueId(), armor);
            }
        }
    }
}