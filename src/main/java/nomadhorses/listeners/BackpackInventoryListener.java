package nomadhorses.listeners;

import nomadhorses.NomadHorses;
import nomadhorses.services.HorseBackpackService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class BackpackInventoryListener implements Listener {
    private final NomadHorses plugin;
    private final HorseBackpackService backpackService;

    public BackpackInventoryListener(NomadHorses plugin) {
        this.plugin = plugin;
        this.backpackService = new HorseBackpackService(plugin);
    }

    @EventHandler
    public void onBackpackClose(InventoryCloseEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(plugin.getLocalizationManager().getMessage("menu.backpack.title"))) {
            if (event.getPlayer() instanceof Player player) {
                backpackService.saveBackpack(player.getUniqueId(), event.getInventory());
            }
        }
    }
}
