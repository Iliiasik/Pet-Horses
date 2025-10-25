package pethorses.services;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pethorses.PetHorses;
import pethorses.storage.HorseData;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class HorseServiceTest {
    private PetHorses plugin;
    private HorseService horseService;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(PetHorses.class);
        horseService = plugin.getHorseService();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testGetHorseData() {
        UUID playerId = UUID.randomUUID();
        HorseData data = horseService.getHorseData(playerId);
        assertNotNull(data);
        assertEquals(playerId, data.getOwnerId());
        assertEquals(1, data.getLevel());
        assertEquals(0, data.getExperience());
    }

    @Test
    public void testCooldownIsOnCooldown() {
        UUID playerId = UUID.randomUUID();
        HorseData data = horseService.getHorseData(playerId);

        assertFalse(horseService.isOnCooldown(data));

        data.setDeathTime(System.currentTimeMillis());
        assertTrue(horseService.isOnCooldown(data));
    }

    @Test
    public void testCooldownLeftFormatted() {
        UUID playerId = UUID.randomUUID();
        HorseData data = horseService.getHorseData(playerId);
        data.setDeathTime(System.currentTimeMillis() - 60000);

        String formatted = horseService.getCooldownLeftFormatted(data);
        assertNotNull(formatted);
        assertTrue(formatted.matches("\\d{2}:\\d{2}"));
    }

    @Test
    public void testAddJump() {
        UUID playerId = UUID.randomUUID();
        HorseData data = horseService.getHorseData(playerId);
        data.setLevel(1);
        data.setExperience(0);
        data.setJumps(0);
        data.setTotalJumps(0);

        horseService.addJump(playerId);

        assertEquals(1, data.getJumps());
        assertEquals(1, data.getTotalJumps());
    }

    @Test
    public void testAddJumpConvertsToXp() {
        UUID playerId = UUID.randomUUID();
        HorseData data = horseService.getHorseData(playerId);
        data.setLevel(1);
        data.setExperience(0);
        data.setJumps(0);
        data.setTotalJumps(0);

        for (int i = 0; i < 10; i++) {
            horseService.addJump(playerId);
        }

        assertEquals(10, data.getTotalJumps());
    }

    @Test
    public void testAddTraveledBlocks() {
        UUID playerId = UUID.randomUUID();
        HorseData data = horseService.getHorseData(playerId);
        data.setBlocksTraveled(0.0);
        data.setTotalBlocksTraveled(0.0);

        horseService.addTraveledBlocks(playerId, 3.5);

        assertEquals(3.5, data.getBlocksTraveled(), 0.01);
        assertEquals(3.5, data.getTotalBlocksTraveled(), 0.01);
    }

    @Test
    public void testAddTraveledBlocksConvertsToXp() {
        UUID playerId = UUID.randomUUID();
        HorseData data = horseService.getHorseData(playerId);
        data.setBlocksTraveled(0.0);
        data.setTotalBlocksTraveled(0.0);

        horseService.addTraveledBlocks(playerId, 7.5);

        assertEquals(7.5, data.getTotalBlocksTraveled(), 0.01);
    }

    @Test
    public void testGetXpRequiredForNextLevel() {
        int req1 = horseService.getXpRequiredForNextLevel(1);
        int req2 = horseService.getXpRequiredForNextLevel(2);

        assertTrue(req1 > 0);
        assertTrue(req2 > req1);
    }

    @Test
    public void testSetFollowing() {
        UUID playerId = UUID.randomUUID();
        HorseData data = horseService.getHorseData(playerId);

        data.setFollowing(true);
        assertTrue(data.isFollowing());

        horseService.setFollowing(data, false);
        assertFalse(data.isFollowing());
    }

    @Test
    public void testHideHorseWithNullHorseId() {
        UUID playerId = UUID.randomUUID();
        HorseData data = horseService.getHorseData(playerId);
        data.setHorseId(null);

        horseService.hideHorse(data);

        assertNull(data.getHorseId());
    }

    @Test
    public void testHorseDataInitialState() {
        UUID playerId = UUID.randomUUID();
        HorseData data = horseService.getHorseData(playerId);

        assertNull(data.getHorseId());
        assertEquals(playerId, data.getOwnerId());
        assertEquals(1, data.getLevel());
        assertEquals(0, data.getExperience());
        assertNull(data.getHorseName());
        assertTrue(data.isFollowing());
    }
}
