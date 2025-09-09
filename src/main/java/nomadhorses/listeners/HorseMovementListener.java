package nomadhorses.listeners;

import nomadhorses.NomadHorses;
import nomadhorses.config.LocalizationManager;
import nomadhorses.services.HorseService;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class HorseMovementListener implements Listener {
    private final NomadHorses plugin;
    private final HorseService horseService;
    private final LocalizationManager localizationManager;

    public HorseMovementListener(NomadHorses plugin) {
        this.plugin = plugin;
        this.horseService = plugin.getHorseService();
        this.localizationManager = plugin.getLocalizationManager();
    }

    @EventHandler
    public void onHorseJump(HorseJumpEvent event) {
        if (!(event.getEntity() instanceof Horse horse) || !horse.isTamed()) return;
        Player owner = (Player) horse.getOwner();
        if (owner == null) return;

        horseService.addJump(owner.getUniqueId());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!(player.getVehicle() instanceof Horse horse) || !horse.isTamed()) return;
        Player owner = (Player) horse.getOwner();
        if (owner == null) return;

        double dx = event.getFrom().getX() - event.getTo().getX();
        double dz = event.getFrom().getZ() - event.getTo().getZ();
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        if (horizontalDistance > 0.0) {
            horseService.addTraveledBlocks(owner.getUniqueId(), horizontalDistance);
        }
    }
}