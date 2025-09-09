package nomadhorses.listeners;

import nomadhorses.NomadHorses;
import nomadhorses.menus.HorseStatsMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuClickListener implements Listener {
    private final NomadHorses plugin;

    public MenuClickListener(NomadHorses plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        String title = event.getView().getTitle();
        if (title.equals(plugin.getLocalizationManager().getMessage("menu.stats.title"))) {
            event.setCancelled(true);
        }
    }
}