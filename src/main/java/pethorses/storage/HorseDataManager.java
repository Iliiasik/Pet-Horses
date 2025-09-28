package pethorses.storage;

import pethorses.PetHorses;
import pethorses.config.ConfigManager;
import pethorses.storage.database.DatabaseStorage;
import pethorses.storage.yaml.YamlStorage;

import java.util.Set;
import java.util.UUID;

public class HorseDataManager {
    private final StorageStrategy storage;

    public HorseDataManager(PetHorses plugin) {
        ConfigManager configManager = new ConfigManager(plugin);
        if (configManager.isDatabaseEnabled()) {
            this.storage = new DatabaseStorage(plugin, configManager);
        } else {
            this.storage = new YamlStorage(plugin);
        }
        this.storage.loadData();
    }

    public HorseData getHorseData(UUID playerId) {
        return storage.getHorseData(playerId);
    }

    public void saveHorseData(HorseData data) {
        storage.saveHorseData(data);
    }

    public void saveAllData() {
        storage.saveAllData();
    }

    public Set<UUID> getPassengers(UUID ownerUUID) {
        return storage.getPassengers(ownerUUID);
    }

    public void addPassenger(UUID ownerUUID, UUID passengerUUID) {
        storage.addPassenger(ownerUUID, passengerUUID);
    }

    public void removePassenger(UUID ownerUUID, UUID passengerUUID) {
        storage.removePassenger(ownerUUID, passengerUUID);
    }

    public void close() {
        if (storage instanceof DatabaseStorage) {
            ((DatabaseStorage) storage).close();
        }
    }
}