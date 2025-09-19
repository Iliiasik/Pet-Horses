package pethorses.listeners;

import pethorses.PetHorses;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class HorseStatsMenuListener implements Listener {
    private final PetHorses plugin;

    public HorseStatsMenuListener(PetHorses plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(plugin.getLocalizationManager().getMessage("menu.stats.title"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(plugin.getLocalizationManager().getMessage("menu.stats.title"))) {
            event.setCancelled(true);
        }
    }
}