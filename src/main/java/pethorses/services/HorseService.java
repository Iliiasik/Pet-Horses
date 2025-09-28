package pethorses.services;

import org.bukkit.util.Vector;
import pethorses.PetHorses;
import pethorses.config.ConfigManager;
import pethorses.storage.HorseData;
import pethorses.storage.HorseDataManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class HorseService {
    private final PetHorses plugin;
    private final HorseDataManager dataManager;
    private final ConfigManager configManager;
    private final HorseBackpackService backpackService;

    public HorseService(PetHorses plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getHorseDataManager();
        this.configManager = plugin.getConfigManager();
        this.backpackService = new HorseBackpackService(plugin);
    }

    public void summonHorse(Player player) {
        UUID playerId = player.getUniqueId();
        if (isOnCooldown(playerId)) {
            player.sendMessage(plugin.getLocalizationManager().getMessage("horse.cooldown_active")
                    .replace("{time}", getCooldownLeftFormatted(playerId)));
            return;
        }

        hideHorse(playerId);
        HorseData data = getHorseData(playerId);
        data.setOwnerId(playerId);

        Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
        horse.setOwner(player);
        horse.setTamed(true);
        horse.setAdult();
        horse.setCanPickupItems(false);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));

        backpackService.handleArmorForHorse(horse, data);

        horse.setColor(data.getColor());
        horse.setStyle(data.getStyle());

        if (data.getHorseName() != null && !data.getHorseName().isEmpty()) {
            horse.setCustomName(data.getHorseNameColor() + data.getHorseName());
            horse.setCustomNameVisible(true);
        }

        applyHorseStats(horse, data.getLevel());
        data.setHorseId(horse.getUniqueId());
        setFollowing(playerId, true);

        if (isFollowing(playerId)) {
            makeHorseFollow(player);
        }
    }

    public void applyHorseStats(Horse horse, int level) {
        double speed = configManager.getSpeedBase() + (configManager.getSpeedMaxBonus() * (level / 20.0));
        horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);

        double maxHealth = configManager.getHealthBase() + (configManager.getHealthMaxBonus() * (level / 20.0));
        horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        horse.setHealth(maxHealth);

        double jumpStrength = configManager.getJumpBase() + (configManager.getJumpMaxBonus() * (level / 20.0));
        horse.getAttribute(Attribute.GENERIC_JUMP_STRENGTH).setBaseValue(jumpStrength);

        HorseData ownerData = getHorseData(((Player) horse.getOwner()).getUniqueId());
        ownerData.setJumps(0);
        ownerData.setBlocksTraveled(0.0);

        if (ownerData.getHorseName() != null && !ownerData.getHorseName().isEmpty()) {
            horse.setCustomName(ownerData.getHorseNameColor() + ownerData.getHorseName());
            horse.setCustomNameVisible(true);
        }
    }

    public void makeHorseFollow(Player player) {
        HorseData data = getHorseData(player.getUniqueId());
        if (data.getHorseId() == null) return;

        Entity entity = Bukkit.getEntity(data.getHorseId());
        if (!(entity instanceof Horse horse)) return;

        horse.getPathfinder().stopPathfinding();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isFollowing(player.getUniqueId()) || horse.isDead() || !player.isOnline()) {
                    cancel();
                    return;
                }

                Location playerLoc = player.getLocation();
                Location horseLoc = horse.getLocation();
                double distance = horseLoc.distance(playerLoc);

                if (distance > 10) {
                    horse.teleport(playerLoc);
                } else if (distance > 2) {
                    Vector direction = playerLoc.toVector().subtract(horseLoc.toVector()).normalize();
                    Location targetLoc = playerLoc.clone().subtract(direction.multiply(2));
                    horse.getPathfinder().moveTo(targetLoc, 1.5);
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    public void hideHorse(UUID playerId) {
        HorseData data = dataManager.getHorseData(playerId);
        if (data == null || data.getHorseId() == null) return;

        Entity entity = Bukkit.getEntity(data.getHorseId());
        if (entity instanceof Horse horse) {
            backpackService.saveHorseArmorFromEntity(playerId, horse);
        }

        if (entity != null) entity.remove();
        data.setHorseId(null);
    }

    public void onHorseDeath(UUID playerId) {
        HorseData data = dataManager.getHorseData(playerId);
        if (data == null) return;

        Entity entity = Bukkit.getEntity(data.getHorseId());
        if (entity instanceof Horse horse) {
            backpackService.saveHorseArmorFromEntity(playerId, horse);
        }

        data.setDeathTime(System.currentTimeMillis());
        data.setHorseId(null);
        dataManager.saveHorseData(data);
    }

    public boolean isOnCooldown(UUID playerId) {
        HorseData data = dataManager.getHorseData(playerId);
        if (data == null) return false;

        long cooldownDuration = configManager.getRespawnCooldownMinutes() * 60 * 1000;
        return System.currentTimeMillis() - data.getDeathTime() < cooldownDuration;
    }

    public String getCooldownLeftFormatted(UUID playerId) {
        HorseData data = dataManager.getHorseData(playerId);
        if (data == null) return "0s";

        long left = (configManager.getRespawnCooldownMinutes() * 60 * 1000 - (System.currentTimeMillis() - data.getDeathTime())) / 1000;
        long minutes = left / 60;
        long seconds = left % 60;
        return minutes + "m " + seconds + "s";
    }

    public void addJump(UUID playerId) {
        HorseData data = getHorseData(playerId);
        data.setJumps(data.getJumps() + 1);
        data.setTotalJumps(data.getTotalJumps() + 1);

        if (data.getJumps() >= 10) {
            int xpToAdd = data.getJumps() / 10;
            data.setJumps(data.getJumps() % 10);
            addExperience(playerId, xpToAdd);
        }
    }

    public void addTraveledBlocks(UUID playerId, double distance) {
        HorseData data = getHorseData(playerId);
        data.setBlocksTraveled(data.getBlocksTraveled() + distance);
        data.setTotalBlocksTraveled(data.getTotalBlocksTraveled() + distance);

        if (data.getBlocksTraveled() >= 5.0) {
            int xpToAdd = (int) (data.getBlocksTraveled() / 5.0);
            data.setBlocksTraveled(data.getBlocksTraveled() % 5.0);
            addExperience(playerId, xpToAdd);
        }
    }

    private void addExperience(UUID playerId, int amount) {
        HorseData data = getHorseData(playerId);
        data.setExperience(data.getExperience() + amount);

        int requiredExp = getXpRequiredForNextLevel(data.getLevel());
        if (data.getExperience() >= requiredExp) {
            data.setLevel(data.getLevel() + 1);
            data.setExperience(data.getExperience() - requiredExp);

            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.sendMessage(plugin.getLocalizationManager().getMessage("horse.level_up")
                        .replace("{level}", String.valueOf(data.getLevel())));

                if (data.getHorseId() != null) {
                    Entity entity = Bukkit.getEntity(data.getHorseId());
                    if (entity instanceof Horse horse) {
                        Location loc = horse.getLocation();
                        horse.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc.add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
                    }
                }
            }

            if (data.getHorseId() != null) {
                Entity entity = Bukkit.getEntity(data.getHorseId());
                if (entity instanceof Horse horse) {
                    applyHorseStats(horse, data.getLevel());
                }
            }
        }
    }

    public int getXpRequiredForNextLevel(int currentLevel) {
        return configManager.getBaseXpForLevel() + (currentLevel * configManager.getXpIncrementPerLevel());
    }

    public HorseData getHorseData(UUID playerId) {
        return dataManager.getHorseData(playerId);
    }

    public boolean isFollowing(UUID playerId) {
        HorseData data = dataManager.getHorseData(playerId);
        return data != null && data.isFollowing();
    }

    public void setFollowing(UUID playerId, boolean following) {
        HorseData data = dataManager.getHorseData(playerId);
        if (data != null) {
            data.setFollowing(following);
        }
    }

    public void saveHorseArmor(UUID playerId, ItemStack armor) {
        backpackService.saveHorseArmor(playerId, armor);
    }
}