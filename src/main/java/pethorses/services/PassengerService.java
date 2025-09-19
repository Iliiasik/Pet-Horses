package pethorses.services;

import pethorses.PetHorses;
import pethorses.storage.HorseDataManager;

import java.util.Set;
import java.util.UUID;

public class PassengerService {
    private final HorseDataManager dataManager;

    public PassengerService(PetHorses plugin) {
        this.dataManager = plugin.getHorseDataManager();
    }

    public boolean hasPermission(UUID ownerUUID, UUID passengerUUID) {
        return dataManager.getPassengers(ownerUUID).contains(passengerUUID);
    }

    public void addPermission(UUID ownerUUID, UUID passengerUUID) {
        dataManager.addPassenger(ownerUUID, passengerUUID);
    }

    public void removePermission(UUID ownerUUID, UUID passengerUUID) {
        dataManager.removePassenger(ownerUUID, passengerUUID);
    }

    public Set<UUID> getPassengers(UUID ownerUUID) {
        return dataManager.getPassengers(ownerUUID);
    }
}