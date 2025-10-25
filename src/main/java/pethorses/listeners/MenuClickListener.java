package pethorses.listeners;

import pethorses.PetHorses;
import pethorses.inventory.MenuHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuClickListener implements Listener {
    private final PetHorses plugin;

    public MenuClickListener(PetHorses plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!(event.getInventory().getHolder() instanceof MenuHolder)) return;
        MenuHolder holder = (MenuHolder) event.getInventory().getHolder();
        if ("stats".equals(holder.getId())) {
            event.setCancelled(true);
        }
    }
}