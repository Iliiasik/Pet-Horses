package pethorses.storage.yaml;

import pethorses.PetHorses;
import pethorses.storage.HorseData;
import pethorses.storage.StorageStrategy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class YamlStorage implements StorageStrategy {
    private final Logger logger;
    private final File dataFile;
    private final FileConfiguration dataConfig;
    private final YamlHorseDataRepository horseRepo;
    private final YamlPassengerRepository passengerRepo;
    private final Map<UUID, HorseData> horsesData = new HashMap<>();
    private final Map<UUID, Set<UUID>> passengerPermissions = new HashMap<>();

    public YamlStorage(PetHorses plugin) {
        this.logger = plugin.getLogger();
        this.dataFile = new File(plugin.getDataFolder(), "horses_data.yml");
        if (!dataFile.exists()) {
            try { dataFile.createNewFile(); }
            catch (IOException e) { logger.severe("Failed to create data file: " + e.getMessage()); }
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        this.horseRepo = new YamlHorseDataRepository(dataConfig);
        this.passengerRepo = new YamlPassengerRepository(dataConfig);
    }

    @Override
    public void loadData() {
        horsesData.clear();
        passengerPermissions.clear();
        for (String key : dataConfig.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(key);
                horsesData.put(playerId, horseRepo.load(playerId));
                passengerPermissions.put(playerId, passengerRepo.load(playerId));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    @Override
    public HorseData getHorseData(UUID playerId) {
        return horsesData.computeIfAbsent(playerId, k -> {
            HorseData hd = new HorseData();
            hd.setOwnerId(playerId);
            return hd;
        });
    }

    @Override
    public void saveHorseData(HorseData data) {
        if (data.getOwnerId() == null) {
            logger.warning("Attempted to save HorseData with null ownerId. Skipping save.");
            return;
        }
        horseRepo.save(data);
        passengerRepo.save(data.getOwnerId(), passengerPermissions.getOrDefault(data.getOwnerId(), Collections.emptySet()));
        saveToFile();
    }

    @Override
    public void saveAllData() {
        dataConfig.getKeys(false).forEach(key -> dataConfig.set(key, null));
        for (Map.Entry<UUID, HorseData> entry : horsesData.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null || entry.getValue().getOwnerId() == null) {
                logger.warning("Skipping save for horse with null ownerId or HorseData.");
                continue;
            }
            horseRepo.save(entry.getValue());
            passengerRepo.save(entry.getKey(), passengerPermissions.getOrDefault(entry.getKey(), Collections.emptySet()));
        }
        saveToFile();
    }

    private void saveToFile() {
        try { dataConfig.save(dataFile); }
        catch (IOException e) { logger.severe("Failed to save horse data: " + e.getMessage()); }
    }

    @Override
    public Set<UUID> getPassengers(UUID ownerUUID) {
        return passengerPermissions.getOrDefault(ownerUUID, Collections.emptySet());
    }

    @Override
    public void addPassenger(UUID ownerUUID, UUID passengerUUID) {
        passengerPermissions.computeIfAbsent(ownerUUID, k -> new HashSet<>()).add(passengerUUID);
    }

    @Override
    public void removePassenger(UUID ownerUUID, UUID passengerUUID) {
        passengerPermissions.getOrDefault(ownerUUID, Collections.emptySet()).remove(passengerUUID);
    }

    public Set<UUID> getAllPlayerIds() {
        return horsesData.keySet();
    }
}