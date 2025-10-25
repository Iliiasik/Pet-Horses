package pethorses.integration;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pethorses.PetHorses;
import pethorses.storage.HorseData;
import pethorses.storage.HorseDataManager;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserScenarioTest {
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
    public void testUserScenario_CustomizeHorseColorAndName_ThenSave() {
        UUID playerId = UUID.randomUUID();
        HorseDataManager dataManager = plugin.getHorseDataManager();

        HorseData horseData = plugin.getHorseService().getHorseData(playerId);
        assertNotNull(horseData);
        assertEquals(playerId, horseData.getOwnerId(), "OwnerId should be set when getting horse data");

        horseData.setColor(org.bukkit.entity.Horse.Color.CHESTNUT);
        horseData.setStyle(org.bukkit.entity.Horse.Style.WHITE);
        horseData.setHorseName("MagicalSteed");

        assertDoesNotThrow(() -> {
            dataManager.saveHorseData(horseData);
        }, "Saving customized horse data should not throw NullPointerException");

        HorseData reloaded = plugin.getHorseService().getHorseData(playerId);
        assertEquals(org.bukkit.entity.Horse.Color.CHESTNUT, reloaded.getColor());
        assertEquals(org.bukkit.entity.Horse.Style.WHITE, reloaded.getStyle());
        assertEquals("MagicalSteed", reloaded.getHorseName());
    }

    @Test
    public void testUserScenario_BackpackClose_NeverCausesNullPointerException() {
        UUID playerId = UUID.randomUUID();

        HorseData data = plugin.getHorseService().getHorseData(playerId);
        assertNotNull(data.getOwnerId(), "Data from service should always have OwnerId");

        assertDoesNotThrow(() -> {
            plugin.getHorseDataManager().saveHorseData(data);
        }, "Saving horse data on backpack close should not throw NullPointerException");
    }

    @Test
    public void testUserScenario_EmeraldButtonClick_SavesChanges() {
        UUID playerId = UUID.randomUUID();

        HorseData horseData = plugin.getHorseService().getHorseData(playerId);

        horseData.setColor(org.bukkit.entity.Horse.Color.BLACK);
        horseData.setHorseName("ShadowHorse");

        assertEquals(playerId, horseData.getOwnerId());
        assertDoesNotThrow(() -> {
            plugin.getHorseDataManager().saveHorseData(horseData);
        });

        HorseData saved = plugin.getHorseService().getHorseData(playerId);
        assertEquals(org.bukkit.entity.Horse.Color.BLACK, saved.getColor());
        assertEquals("ShadowHorse", saved.getHorseName());
    }

    @Test
    public void testUserScenario_MultipleCustomizationsSaved() {
        UUID playerId = UUID.randomUUID();
        HorseDataManager dataManager = plugin.getHorseDataManager();

        org.bukkit.entity.Horse.Color[] colors = {
            org.bukkit.entity.Horse.Color.WHITE,
            org.bukkit.entity.Horse.Color.CREAMY,
            org.bukkit.entity.Horse.Color.CHESTNUT,
            org.bukkit.entity.Horse.Color.BROWN,
            org.bukkit.entity.Horse.Color.BLACK
        };

        for (org.bukkit.entity.Horse.Color color : colors) {
            HorseData data = plugin.getHorseService().getHorseData(playerId);
            data.setColor(color);
            data.setHorseName("Horse_" + color.name());

            assertDoesNotThrow(() -> {
                dataManager.saveHorseData(data);
            });

            HorseData reloaded = plugin.getHorseService().getHorseData(playerId);
            assertEquals(color, reloaded.getColor());
        }
    }

    @Test
    public void testUserScenario_EmeraldButtonWithoutHorseIdDoesNotCrash() {
        UUID playerId = UUID.randomUUID();

        HorseData data = plugin.getHorseService().getHorseData(playerId);
        data.setColor(org.bukkit.entity.Horse.Color.WHITE);
        data.setHorseName("NewHorse");
        data.setHorseId(null);

        assertNull(data.getHorseId());
        assertNotNull(data.getOwnerId());

        assertDoesNotThrow(() -> {
            plugin.getHorseDataManager().saveHorseData(data);
        });
    }

    @Test
    public void testUserScenario_YamlRepoSkipsNullOwnerIdSafely() {
        org.bukkit.configuration.file.YamlConfiguration config = new org.bukkit.configuration.file.YamlConfiguration();
        pethorses.storage.yaml.YamlHorseDataRepository repo = new pethorses.storage.yaml.YamlHorseDataRepository(config);

        HorseData dataWithoutOwner = new HorseData();
        dataWithoutOwner.setLevel(5);
        dataWithoutOwner.setHorseName("Test");

        assertNull(dataWithoutOwner.getOwnerId());
        assertDoesNotThrow(() -> {
            repo.save(dataWithoutOwner);
        });
    }
}

