package nomadhorses.listeners;

import nomadhorses.NomadHorses;
import nomadhorses.services.HorseBackpackService;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class HorseBackpackListener implements Listener {
    private final NomadHorses plugin;
    private final HorseBackpackService backpackService;

    public HorseBackpackListener(NomadHorses plugin) {
        this.plugin = plugin;
        this.backpackService = new HorseBackpackService(plugin);
    }

    @EventHandler
    public void onHorseShiftClick(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Horse horse) || !horse.isTamed()) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();

        if (player.isSneaking() && player.getInventory().getItemInMainHand().getType().isAir()) {
            event.setCancelled(true);
            backpackService.openBackpack(player, horse);
        }
    }
}