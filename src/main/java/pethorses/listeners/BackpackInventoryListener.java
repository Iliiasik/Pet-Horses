package pethorses.listeners;

import pethorses.PetHorses;
import pethorses.inventory.MenuHolder;
import pethorses.services.HorseBackpackService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.UUID;

public class BackpackInventoryListener implements Listener {
    private final HorseBackpackService backpackService;

    public BackpackInventoryListener(PetHorses plugin) {
        this.backpackService = new HorseBackpackService(plugin);
    }

    @EventHandler
    public void onBackpackClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!(event.getInventory().getHolder() instanceof MenuHolder)) return;
        MenuHolder holder = (MenuHolder) event.getInventory().getHolder();
        String id = holder.getId();
        if (id != null && id.startsWith("backpack_")) {
            String maybeUuid = id.substring("backpack_".length());
            UUID ownerUuid;
            try {
                ownerUuid = UUID.fromString(maybeUuid);
            } catch (Exception e) {
                ownerUuid = player.getUniqueId();
            }
            backpackService.saveBackpack(ownerUuid, event.getInventory());
        }
    }
}
