package pethorses.storage.database;

import pethorses.PetHorses;
import pethorses.config.ConfigManager;
import pethorses.storage.HorseData;
import pethorses.storage.StorageStrategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class DatabaseStorage implements StorageStrategy {
    private final DatabaseManager dbManager;
    private final HorseDataRepository horseRepo;
    private final PassengerRepository passengerRepo;

    private final Map<UUID, HorseData> horsesData = new ConcurrentHashMap<>();
    private final Map<UUID, Set<UUID>> passengerPermissions = new ConcurrentHashMap<>();

    public DatabaseStorage(PetHorses plugin, ConfigManager configManager) {
        Logger logger = plugin.getLogger();
        this.dbManager = new DatabaseManager(configManager);
        this.horseRepo = new HorseDataRepository(dbManager, logger);
        this.passengerRepo = new PassengerRepository(dbManager, logger);
        loadData();
    }

    @Override
    public void loadData() {
        horsesData.clear();
        horsesData.putAll(horseRepo.loadAll());
        passengerPermissions.clear();
        passengerPermissions.putAll(passengerRepo.loadAll());
    }

    @Override
    public HorseData getHorseData(UUID playerId) {
        return horsesData.computeIfAbsent(playerId, k -> new HorseData());
    }

    @Override
    public void saveHorseData(HorseData data) {
        horsesData.put(data.getOwnerId(), data);
        horseRepo.save(data);
    }

    @Override
    public void saveAllData() {
        horsesData.values().forEach(horseRepo::save);
    }

    @Override
    public Set<UUID> getPassengers(UUID ownerUUID) {
        return passengerPermissions.getOrDefault(ownerUUID, Collections.emptySet());
    }

    @Override
    public void addPassenger(UUID ownerUUID, UUID passengerUUID) {
        passengerPermissions.computeIfAbsent(ownerUUID, k -> ConcurrentHashMap.newKeySet()).add(passengerUUID);
        passengerRepo.addPassenger(ownerUUID, passengerUUID);
    }

    @Override
    public void removePassenger(UUID ownerUUID, UUID passengerUUID) {
        Set<UUID> passengers = passengerPermissions.get(ownerUUID);
        if (passengers != null) passengers.remove(passengerUUID);
        passengerRepo.removePassenger(ownerUUID, passengerUUID);
    }

    public void close() {
        dbManager.close();
    }
}
