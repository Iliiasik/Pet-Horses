package pethorses.listeners;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pethorses.PetHorses;
import pethorses.storage.HorseData;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class BackpackInventoryListenerTest {
    private PetHorses plugin;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(PetHorses.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testHorseDataAlwaysHasOwnerIdBeforeSave() {
        UUID playerId = UUID.randomUUID();
        HorseData data = plugin.getHorseService().getHorseData(playerId);

        assertNotNull(data.getOwnerId(), "HorseData should have OwnerId set from service");
        assertEquals(playerId, data.getOwnerId());
    }

    @Test
    public void testBackpackServiceSaveDoesNotThrowNullPointerException() {
        UUID playerId = UUID.randomUUID();
        HorseData data = plugin.getHorseService().getHorseData(playerId);

        assertNotNull(data.getOwnerId());
        assertDoesNotThrow(() -> {
            plugin.getHorseDataManager().saveHorseData(data);
        });
    }

    @Test
    public void testHorseDataFromServiceAlwaysHasOwnerId() {
        UUID[] playerIds = {
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()
        };

        for (UUID playerId : playerIds) {
            HorseData data = plugin.getHorseService().getHorseData(playerId);
            assertNotNull(data.getOwnerId(), "OwnerId must not be null for player " + playerId);
            assertEquals(playerId, data.getOwnerId());
        }
    }

    @Test
    public void testYamlRepositorySaveSkipsNullOwnerIdGracefully() {
        HorseData dataWithNullId = new HorseData();
        dataWithNullId.setLevel(5);
        dataWithNullId.setHorseName("Test");

        assertNull(dataWithNullId.getOwnerId());
        assertDoesNotThrow(() -> {
            plugin.getHorseDataManager().saveHorseData(dataWithNullId);
        });
    }

    @Test
    public void testBackpackServiceReceivesValidHorseData() {
        UUID playerId = UUID.randomUUID();
        HorseData data = plugin.getHorseService().getHorseData(playerId);

        assertTrue(data.getOwnerId() != null, "Service should provide data with valid OwnerId");
        assertEquals(playerId, data.getOwnerId());
    }

    @Test
    public void testMultipleBackpackSavesWithValidOwnerId() {
        UUID playerId = UUID.randomUUID();

        for (int i = 0; i < 5; i++) {
            HorseData data = plugin.getHorseService().getHorseData(playerId);
            assertNotNull(data.getOwnerId());
            assertDoesNotThrow(() -> {
                plugin.getHorseDataManager().saveHorseData(data);
            });
        }
    }
}

