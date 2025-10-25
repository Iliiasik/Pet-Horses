package pethorses.services;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pethorses.PetHorses;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PassengerServiceTest {
    private PetHorses plugin;
    private PassengerService passengerService;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(PetHorses.class);
        passengerService = plugin.getPassengerService();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testAddPermission() {
        UUID owner = UUID.randomUUID();
        UUID passenger = UUID.randomUUID();

        passengerService.addPermission(owner, passenger);

        assertTrue(passengerService.hasPermission(owner, passenger));
    }

    @Test
    public void testRemovePermission() {
        UUID owner = UUID.randomUUID();
        UUID passenger = UUID.randomUUID();

        passengerService.addPermission(owner, passenger);
        assertTrue(passengerService.hasPermission(owner, passenger));

        passengerService.removePermission(owner, passenger);
        assertFalse(passengerService.hasPermission(owner, passenger));
    }

    @Test
    public void testHasPermissionInitiallyFalse() {
        UUID owner = UUID.randomUUID();
        UUID passenger = UUID.randomUUID();

        assertFalse(passengerService.hasPermission(owner, passenger));
    }

    @Test
    public void testGetPassengersEmpty() {
        UUID owner = UUID.randomUUID();
        Set<UUID> passengers = passengerService.getPassengers(owner);

        assertNotNull(passengers);
        assertTrue(passengers.isEmpty());
    }

    @Test
    public void testGetPassengersWithMultiplePassengers() {
        UUID owner = UUID.randomUUID();
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        UUID p3 = UUID.randomUUID();

        passengerService.addPermission(owner, p1);
        passengerService.addPermission(owner, p2);
        passengerService.addPermission(owner, p3);

        Set<UUID> passengers = passengerService.getPassengers(owner);

        assertEquals(3, passengers.size());
        assertTrue(passengers.contains(p1));
        assertTrue(passengers.contains(p2));
        assertTrue(passengers.contains(p3));
    }

    @Test
    public void testRemoveOnePassengerFromMultiple() {
        UUID owner = UUID.randomUUID();
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();

        passengerService.addPermission(owner, p1);
        passengerService.addPermission(owner, p2);

        passengerService.removePermission(owner, p1);

        Set<UUID> passengers = passengerService.getPassengers(owner);
        assertEquals(1, passengers.size());
        assertFalse(passengers.contains(p1));
        assertTrue(passengers.contains(p2));
    }

    @Test
    public void testMultipleOwnersIndependent() {
        UUID owner1 = UUID.randomUUID();
        UUID owner2 = UUID.randomUUID();
        UUID passenger1 = UUID.randomUUID();
        UUID passenger2 = UUID.randomUUID();

        passengerService.addPermission(owner1, passenger1);
        passengerService.addPermission(owner2, passenger2);

        assertTrue(passengerService.hasPermission(owner1, passenger1));
        assertFalse(passengerService.hasPermission(owner1, passenger2));
        assertFalse(passengerService.hasPermission(owner2, passenger1));
        assertTrue(passengerService.hasPermission(owner2, passenger2));
    }
}
