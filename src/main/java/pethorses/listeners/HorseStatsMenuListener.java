package pethorses.listeners;

import pethorses.PetHorses;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class HorseStatsMenuListener implements Listener {
    private final PetHorses plugin;

    public HorseStatsMenuListener(PetHorses plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (LegacyComponentSerializer.legacySection().serialize(event.getView().title()).equals(plugin.getLocalizationManager().getMessage("menu.stats.title"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (LegacyComponentSerializer.legacySection().serialize(event.getView().title()).equals(plugin.getLocalizationManager().getMessage("menu.stats.title"))) {
            event.setCancelled(true);
        }
    }
}