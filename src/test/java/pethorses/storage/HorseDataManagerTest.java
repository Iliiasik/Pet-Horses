package pethorses.storage;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pethorses.PetHorses;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class HorseDataManagerTest {
    private PetHorses plugin;
    private HorseDataManager dataManager;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(PetHorses.class);
        dataManager = plugin.getHorseDataManager();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testGetHorseData() {
        UUID playerId = UUID.randomUUID();
        HorseData data = dataManager.getHorseData(playerId);
        assertNotNull(data);
        assertEquals(playerId, data.getOwnerId());
    }

    @Test
    public void testGetHorseDataConsistency() {
        UUID playerId = UUID.randomUUID();
        HorseData data1 = dataManager.getHorseData(playerId);
        HorseData data2 = dataManager.getHorseData(playerId);
        assertEquals(data1, data2);
    }

    @Test
    public void testSaveHorseData() {
        UUID playerId = UUID.randomUUID();
        HorseData data = dataManager.getHorseData(playerId);
        data.setLevel(5);
        data.setExperience(100);

        dataManager.saveHorseData(data);

        HorseData loaded = dataManager.getHorseData(playerId);
        assertEquals(5, loaded.getLevel());
    }

    @Test
    public void testGetPassengersEmpty() {
        UUID owner = UUID.randomUUID();
        Set<UUID> passengers = dataManager.getPassengers(owner);
        assertNotNull(passengers);
        assertTrue(passengers.isEmpty());
    }

    @Test
    public void testAddPassenger() {
        UUID owner = UUID.randomUUID();
        UUID passenger = UUID.randomUUID();

        dataManager.addPassenger(owner, passenger);

        Set<UUID> passengers = dataManager.getPassengers(owner);
        assertTrue(passengers.contains(passenger));
    }

    @Test
    public void testRemovePassenger() {
        UUID owner = UUID.randomUUID();
        UUID passenger = UUID.randomUUID();

        dataManager.addPassenger(owner, passenger);
        dataManager.removePassenger(owner, passenger);

        Set<UUID> passengers = dataManager.getPassengers(owner);
        assertFalse(passengers.contains(passenger));
    }

    @Test
    public void testAddMultiplePassengers() {
        UUID owner = UUID.randomUUID();
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        UUID p3 = UUID.randomUUID();

        dataManager.addPassenger(owner, p1);
        dataManager.addPassenger(owner, p2);
        dataManager.addPassenger(owner, p3);

        Set<UUID> passengers = dataManager.getPassengers(owner);
        assertEquals(3, passengers.size());
    }
}

