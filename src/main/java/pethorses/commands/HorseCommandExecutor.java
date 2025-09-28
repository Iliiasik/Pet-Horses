package pethorses.commands;

import pethorses.PetHorses;
import pethorses.config.LocalizationManager;
import pethorses.services.HorseService;
import pethorses.services.PassengerService;
import pethorses.menus.HorseCustomizationMenu;
import pethorses.menus.HorseStatsMenu;
import pethorses.storage.HorseData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class HorseCommandExecutor implements CommandExecutor {
    private final PetHorses plugin;
    private final HorseService horseService;
    private final PassengerService passengerService;
    private final LocalizationManager localizationManager;

    public HorseCommandExecutor(PetHorses plugin) {
        this.plugin = plugin;
        this.horseService = plugin.getHorseService();
        this.passengerService = plugin.getPassengerService();
        this.localizationManager = plugin.getLocalizationManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(localizationManager.getMessage("error.players_only"));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("stats")) {
            HorseStatsMenu.open(player, plugin);
            return true;
        }

        HorseData data = horseService.getHorseData(player.getUniqueId());

        switch (args[0].toLowerCase()) {
            case "summon":
                horseService.summonHorse(player);
                break;
            case "hide":
                horseService.hideHorse(data);
                player.sendMessage(localizationManager.getMessage("horse.hidden"));
                break;
            case "follow":
                horseService.setFollowing(data, true);
                if (data.getHorseId() != null) {
                    Entity entity = Bukkit.getEntity(data.getHorseId());
                    if (entity instanceof Horse horse) {
                        horseService.makeHorseFollow(player, data, horse);
                    }
                }
                player.sendMessage(localizationManager.getMessage("horse.following"));
                break;
            case "stay":
                horseService.setFollowing(data, false);
                player.sendMessage(localizationManager.getMessage("horse.staying"));
                break;
            case "customize":
                HorseCustomizationMenu.open(player, plugin);
                break;
            case "allowpassenger":
                handleAllowPassenger(player, args);
                break;
            case "removepassenger":
                handleRemovePassenger(player, args);
                break;
            case "passengers":
                showPassengers(player);
                break;
            case "help":
                sendHelpMessage(player);
                break;
            default:
                player.sendMessage(localizationManager.getMessage("error.unknown_command"));
                sendHelpMessage(player);
                break;
        }
        return true;
    }

    private void handleAllowPassenger(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(localizationManager.getMessage("passenger.usage_allow"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(localizationManager.getMessage("error.player_not_found"));
            return;
        }

        passengerService.addPermission(player.getUniqueId(), target.getUniqueId());
        player.sendMessage(localizationManager.getMessage("passenger.allowed")
                .replace("{player}", target.getName()));
        target.sendMessage(localizationManager.getMessage("passenger.informed_allowed")
                .replace("{owner}", player.getName()));
    }

    private void handleRemovePassenger(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(localizationManager.getMessage("passenger.usage_remove"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(localizationManager.getMessage("error.player_not_found"));
            return;
        }

        passengerService.removePermission(player.getUniqueId(), target.getUniqueId());
        player.sendMessage(localizationManager.getMessage("passenger.removed")
                .replace("{player}", target.getName()));
        target.sendMessage(localizationManager.getMessage("passenger.informed_removed")
                .replace("{owner}", player.getName()));
    }

    private void showPassengers(Player player) {
        Set<UUID> passengers = passengerService.getPassengers(player.getUniqueId());
        if (passengers.isEmpty()) {
            player.sendMessage(localizationManager.getMessage("passenger.list_empty"));
        } else {
            player.sendMessage(localizationManager.getMessage("passenger.list_header"));
            for (UUID passengerId : passengers) {
                Player p = Bukkit.getPlayer(passengerId);
                if (p != null) {
                    player.sendMessage(localizationManager.getMessage("passenger.list_item")
                            .replace("{player}", p.getName()));
                }
            }
        }
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(localizationManager.getMessage("help.title"));
        player.sendMessage(localizationManager.getMessage("help.stats"));
        player.sendMessage(localizationManager.getMessage("help.summon"));
        player.sendMessage(localizationManager.getMessage("help.hide"));
        player.sendMessage(localizationManager.getMessage("help.follow"));
        player.sendMessage(localizationManager.getMessage("help.stay"));
        player.sendMessage(localizationManager.getMessage("help.customize"));
        player.sendMessage(localizationManager.getMessage("help.allowpassenger"));
        player.sendMessage(localizationManager.getMessage("help.removepassenger"));
        player.sendMessage(localizationManager.getMessage("help.passengers"));
        player.sendMessage(localizationManager.getMessage("help.help"));
    }
}