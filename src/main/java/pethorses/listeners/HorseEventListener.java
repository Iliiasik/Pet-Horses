package pethorses.listeners;

import pethorses.PetHorses;
import pethorses.config.LocalizationManager;
import pethorses.services.HorseService;
import pethorses.services.PassengerService;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class HorseEventListener implements Listener {
    private final PetHorses plugin;
    private final HorseService horseService;
    private final PassengerService passengerService;
    private final LocalizationManager localizationManager;

    public HorseEventListener(PetHorses plugin) {
        this.plugin = plugin;
        this.horseService = plugin.getHorseService();
        this.passengerService = plugin.getPassengerService();
        this.localizationManager = plugin.getLocalizationManager();
    }

    @EventHandler
    public void onHorseDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Horse horse) || !horse.isTamed()) return;

        Player owner = (Player) horse.getOwner();
        if (owner == null) return;

        horseService.onHorseDeath(owner.getUniqueId());
        owner.sendMessage(localizationManager.getMessage("horse.death_cooldown")
                .replace("{minutes}", String.valueOf(plugin.getConfigManager().getRespawnCooldownMinutes())));
    }

    @EventHandler
    public void onHorseDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Horse horse) || !horse.isTamed()) return;
        if (!(event.getDamager() instanceof Player attacker)) return;

        Player owner = (Player) horse.getOwner();
        if (owner == null || attacker.getUniqueId().equals(owner.getUniqueId())) return;

        event.setCancelled(true);
        attacker.sendMessage(localizationManager.getMessage("horse.not_yours"));
    }

    @EventHandler
    public void onHorseInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Horse horse) || !horse.isTamed()) return;

        Player player = event.getPlayer();
        Player owner = (Player) horse.getOwner();
        if (owner == null) return;

        if (player.getUniqueId().equals(owner.getUniqueId())) return;

        if (!passengerService.hasPermission(owner.getUniqueId(), player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(localizationManager.getMessage("passenger.no_permission"));
            return;
        }

        if (!horse.getPassengers().isEmpty()) {
            event.setCancelled(true);
            player.sendMessage(localizationManager.getMessage("horse.already_has_passenger"));
            return;
        }

        event.setCancelled(true);
        horse.addPassenger(player);
        player.sendMessage(localizationManager.getMessage("horse.passenger_mounted"));
        owner.sendMessage(localizationManager.getMessage("horse.passenger_informed_owner")
                .replace("{passenger}", player.getName()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        horseService.hideHorse(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onFallDamage(org.bukkit.event.entity.EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Horse)) return;
        if (event.getCause() != org.bukkit.event.entity.EntityDamageEvent.DamageCause.FALL) return;
        event.setCancelled(true);
    }
}