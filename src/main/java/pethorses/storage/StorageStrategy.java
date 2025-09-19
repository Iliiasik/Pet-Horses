package pethorses.storage;

import java.util.Set;
import java.util.UUID;

public interface StorageStrategy {
    void loadData();
    HorseData getHorseData(UUID playerId);
    void saveHorseData(HorseData data);
    void saveAllData();
    Set<UUID> getPassengers(UUID ownerUUID);
    void addPassenger(UUID ownerUUID, UUID passengerUUID);
    void removePassenger(UUID ownerUUID, UUID passengerUUID);
}