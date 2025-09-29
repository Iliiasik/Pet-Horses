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
        HorseData data = getHorseData(playerId);

        if (isOnCooldown(data)) {
            player.sendMessage(plugin.getLocalizationManager().getMessage("horse.cooldown_active")
                    .replace("{time}", getCooldownLeftFormatted(data)));
            return;
        }

        hideHorse(data);
        data.setOwnerId(playerId);

        Horse horse = spawnHorse(player.getLocation(), player, data);
        setFollowing(data, true);

        if (data.isFollowing()) {
            makeHorseFollow(player, data, horse);
        }
    }

    private Horse spawnHorse(Location location, Player owner, HorseData data) {
        Horse horse = (Horse) owner.getWorld().spawnEntity(location, EntityType.HORSE);
        horse.setOwner(owner);
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

        applyHorseStats(horse, data);
        data.setHorseId(horse.getUniqueId());
        return horse;
    }

    public void applyHorseStats(Horse horse, HorseData data) {
        int level = data.getLevel();
        horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
                .setBaseValue(configManager.getSpeedBase() + (configManager.getSpeedMaxBonus() * (level / 20.0)));
        double maxHealth = configManager.getHealthBase() + (configManager.getHealthMaxBonus() * (level / 20.0));
        horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        horse.setHealth(maxHealth);
        horse.getAttribute(Attribute.GENERIC_JUMP_STRENGTH)
                .setBaseValue(configManager.getJumpBase() + (configManager.getJumpMaxBonus() * (level / 20.0)));

        data.setJumps(0);
        data.setBlocksTraveled(0.0);

        if (data.getHorseName() != null && !data.getHorseName().isEmpty()) {
            horse.setCustomName(data.getHorseNameColor() + data.getHorseName());
            horse.setCustomNameVisible(true);
        }
    }

    public void makeHorseFollow(Player player, HorseData data, Horse horse) {
        horse.getPathfinder().stopPathfinding();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!data.isFollowing() || horse.isDead() || !player.isOnline()) {
                    cancel();
                    return;
                }
                Location playerLoc = player.getLocation();
                Location horseLoc = horse.getLocation();
                double distance = horseLoc.distance(playerLoc);

                double speed = player.isSprinting() ? 2.5 : 1.5;

                if (distance > 10) {
                    horse.teleport(playerLoc);
                } else if (distance > 2) {
                    Vector direction = playerLoc.toVector().subtract(horseLoc.toVector()).normalize();
                    Location targetLoc = playerLoc.clone().subtract(direction.multiply(2));
                    horse.getPathfinder().moveTo(targetLoc, speed);
                }
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    public void hideHorse(HorseData data) {
        if (data == null || data.getHorseId() == null) return;
        Entity entity = Bukkit.getEntity(data.getHorseId());
        if (entity instanceof Horse horse) {
            backpackService.saveHorseArmorFromEntity(data.getOwnerId(), horse);
        }
        if (entity != null) entity.remove();
        data.setHorseId(null);
    }

    public void onHorseDeath(UUID playerId, Horse horse) {
        HorseData data = getHorseData(playerId);
        if (data == null) return;

        if (configManager.isBackpackDropOnDeath()) {
            dropBackpackItems(horse, data);
            data.setBackpackItems(new ItemStack[0]);
        }
        backpackService.saveHorseArmorFromEntity(playerId, horse);

        data.setDeathTime(System.currentTimeMillis());
        data.setHorseId(null);

        new BukkitRunnable() {
            @Override
            public void run() {
                dataManager.saveHorseData(data);
            }
        }.runTaskAsynchronously(plugin);
    }

    private void dropBackpackItems(Horse horse, HorseData data) {
        ItemStack[] items = data.getBackpackItems();
        if (items == null) return;
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                horse.getWorld().dropItem(horse.getLocation(), item);
            }
        }
    }

    public boolean isOnCooldown(HorseData data) {
        if (data == null) return false;
        long cooldownDuration = configManager.getRespawnCooldownMinutes() * 60 * 1000;
        return System.currentTimeMillis() - data.getDeathTime() < cooldownDuration;
    }

    public String getCooldownLeftFormatted(HorseData data) {
        if (data == null) return "00:00";
        long left = (configManager.getRespawnCooldownMinutes() * 60 * 1000 - (System.currentTimeMillis() - data.getDeathTime())) / 1000;
        long minutes = left / 60;
        long seconds = left % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void addJump(UUID playerId) {
        HorseData data = getHorseData(playerId);
        if (data == null) return;
        data.setJumps(data.getJumps() + 1);
        data.setTotalJumps(data.getTotalJumps() + 1);

        if (data.getJumps() >= 10) {
            int xpToAdd = data.getJumps() / 10;
            data.setJumps(data.getJumps() % 10);
            addExperience(data, xpToAdd);
        }
    }

    public void addTraveledBlocks(UUID playerId, double distance) {
        HorseData data = getHorseData(playerId);
        if (data == null) return;
        data.setBlocksTraveled(data.getBlocksTraveled() + distance);
        data.setTotalBlocksTraveled(data.getTotalBlocksTraveled() + distance);

        if (data.getBlocksTraveled() >= 5.0) {
            int xpToAdd = (int) (data.getBlocksTraveled() / 5.0);
            data.setBlocksTraveled(data.getBlocksTraveled() % 5.0);
            addExperience(data, xpToAdd);
        }
    }

    private void addExperience(HorseData data, int amount) {
        data.setExperience(data.getExperience() + amount);

        int requiredExp = getXpRequiredForNextLevel(data.getLevel());
        if (data.getExperience() >= requiredExp) {
            data.setLevel(data.getLevel() + 1);
            data.setExperience(data.getExperience() - requiredExp);

            Player player = Bukkit.getPlayer(data.getOwnerId());
            if (player != null) {
                player.sendMessage(plugin.getLocalizationManager().getMessage("horse.level_up")
                        .replace("{level}", String.valueOf(data.getLevel())));
                if (data.getHorseId() != null) {
                    Entity entity = Bukkit.getEntity(data.getHorseId());
                    if (entity instanceof Horse horse) {
                        Location loc = horse.getLocation();
                        horse.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, loc.add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0.1);
                        applyHorseStats(horse, data);
                    }
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

    public void setFollowing(HorseData data, boolean following) {
        if (data != null) {
            data.setFollowing(following);
        }
    }

    public void saveHorseArmor(UUID playerId, ItemStack armor) {
        backpackService.saveHorseArmor(playerId, armor);
    }
}