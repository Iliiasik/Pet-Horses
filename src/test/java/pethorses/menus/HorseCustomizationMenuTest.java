package pethorses.menus;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pethorses.PetHorses;
import pethorses.storage.HorseData;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class HorseCustomizationMenuTest {
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
    public void testHorseDataHasOwnerIdAfterCustomization() {
        UUID playerId = UUID.randomUUID();
        HorseData data = plugin.getHorseService().getHorseData(playerId);

        assertNotNull(data);
        assertEquals(playerId, data.getOwnerId());
    }

    @Test
    public void testHorseDataOwnerIdNotNullWhenSaving() {
        UUID playerId = UUID.randomUUID();
        HorseData data = plugin.getHorseService().getHorseData(playerId);
        data.setColor(org.bukkit.entity.Horse.Color.BLACK);
        data.setHorseName("TestHorse");

        assertNotNull(data.getOwnerId(), "OwnerId must not be null before saving");
        assertEquals(playerId, data.getOwnerId());

        plugin.getHorseDataManager().saveHorseData(data);
    }

    @Test
    public void testHorseDataCanBeSavedWithoutNullPointerException() {
        UUID playerId = UUID.randomUUID();
        HorseData data = plugin.getHorseService().getHorseData(playerId);
        data.setHorseName("CustomHorse");
        data.setColor(org.bukkit.entity.Horse.Color.BROWN);

        assertDoesNotThrow(() -> {
            plugin.getHorseDataManager().saveHorseData(data);
        });
    }

    @Test
    public void testHorseDataColorChangePreserved() {
        UUID playerId = UUID.randomUUID();
        HorseData data = plugin.getHorseService().getHorseData(playerId);

        data.setColor(org.bukkit.entity.Horse.Color.CHESTNUT);
        assertEquals(org.bukkit.entity.Horse.Color.CHESTNUT, data.getColor());

        plugin.getHorseDataManager().saveHorseData(data);

        HorseData reloadedData = plugin.getHorseService().getHorseData(playerId);
        assertEquals(org.bukkit.entity.Horse.Color.CHESTNUT, reloadedData.getColor());
    }

    @Test
    public void testHorseDataNameChangePreserved() {
        UUID playerId = UUID.randomUUID();
        HorseData data = plugin.getHorseService().getHorseData(playerId);

        data.setHorseName("Shadow");
        assertEquals("Shadow", data.getHorseName());

        plugin.getHorseDataManager().saveHorseData(data);

        HorseData reloadedData = plugin.getHorseService().getHorseData(playerId);
        assertEquals("Shadow", reloadedData.getHorseName());
    }

    @Test
    public void testHorseDataMultipleCustomizationsPreserved() {
        UUID playerId = UUID.randomUUID();
        HorseData data = plugin.getHorseService().getHorseData(playerId);

        data.setColor(org.bukkit.entity.Horse.Color.WHITE);
        data.setStyle(org.bukkit.entity.Horse.Style.WHITE);
        data.setHorseName("Snowflake");

        plugin.getHorseDataManager().saveHorseData(data);

        HorseData reloadedData = plugin.getHorseService().getHorseData(playerId);
        assertEquals(org.bukkit.entity.Horse.Color.WHITE, reloadedData.getColor());
        assertEquals(org.bukkit.entity.Horse.Style.WHITE, reloadedData.getStyle());
        assertEquals("Snowflake", reloadedData.getHorseName());
    }

    @Test
    public void testYamlRepositorySavesValidDataWithOwnerId() {
        UUID playerId = UUID.randomUUID();
        HorseData data = new HorseData();
        data.setOwnerId(playerId);
        data.setLevel(5);
        data.setHorseName("TestHorse");
        data.setColor(org.bukkit.entity.Horse.Color.BLACK);

        assertDoesNotThrow(() -> {
            plugin.getHorseDataManager().saveHorseData(data);
        });
    }

    @Test
    public void testYamlRepositorySkipsNullOwnerIdSafely() {
        HorseData data = new HorseData();
        data.setLevel(5);
        data.setHorseName("TestHorse");

        assertNull(data.getOwnerId());
        assertDoesNotThrow(() -> {
            plugin.getHorseDataManager().saveHorseData(data);
        });
    }
}

