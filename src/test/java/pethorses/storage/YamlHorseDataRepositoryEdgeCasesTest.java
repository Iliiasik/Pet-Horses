package pethorses.storage;

import org.junit.jupiter.api.Test;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Horse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class YamlHorseDataRepositoryEdgeCasesTest {

    @Test
    public void testSaveWithNullOwnerIdDoesNotThrow() {
        YamlConfiguration config = new YamlConfiguration();
        pethorses.storage.yaml.YamlHorseDataRepository repo = new pethorses.storage.yaml.YamlHorseDataRepository(config);

        HorseData dataWithNullId = new HorseData();
        dataWithNullId.setLevel(10);
        dataWithNullId.setHorseName("Test");
        dataWithNullId.setColor(Horse.Color.BLACK);

        assertNull(dataWithNullId.getOwnerId());
        assertDoesNotThrow(() -> repo.save(dataWithNullId));
    }

    @Test
    public void testSaveWithValidOwnerIdSucceeds() {
        YamlConfiguration config = new YamlConfiguration();
        pethorses.storage.yaml.YamlHorseDataRepository repo = new pethorses.storage.yaml.YamlHorseDataRepository(config);

        UUID ownerId = UUID.randomUUID();
        HorseData data = new HorseData();
        data.setOwnerId(ownerId);
        data.setLevel(5);
        data.setHorseName("Shadow");
        data.setColor(Horse.Color.BROWN);

        assertDoesNotThrow(() -> repo.save(data));
    }

    @Test
    public void testLoadReturnsValidData() {
        YamlConfiguration config = new YamlConfiguration();
        pethorses.storage.yaml.YamlHorseDataRepository repo = new pethorses.storage.yaml.YamlHorseDataRepository(config);

        UUID randomId = UUID.randomUUID();
        HorseData loaded = repo.load(randomId);

        assertNotNull(loaded);
        assertEquals(1, loaded.getLevel());
        assertEquals(0, loaded.getExperience());
    }

    @Test
    public void testSaveAndLoadPreservesAllData() {
        YamlConfiguration config = new YamlConfiguration();
        pethorses.storage.yaml.YamlHorseDataRepository repo = new pethorses.storage.yaml.YamlHorseDataRepository(config);

        UUID ownerId = UUID.randomUUID();
        HorseData original = new HorseData();
        original.setOwnerId(ownerId);
        original.setLevel(7);
        original.setExperience(150);
        original.setHorseName("Phoenix");
        original.setColor(Horse.Color.CHESTNUT);
        original.setStyle(Horse.Style.WHITE);

        repo.save(original);
        HorseData loaded = repo.load(ownerId);

        assertEquals(7, loaded.getLevel());
        assertEquals(150, loaded.getExperience());
        assertEquals("Phoenix", loaded.getHorseName());
        assertEquals(Horse.Color.CHESTNUT, loaded.getColor());
        assertEquals(Horse.Style.WHITE, loaded.getStyle());
    }

    @Test
    public void testMultipleSavesWithDifferentOwnerIds() {
        YamlConfiguration config = new YamlConfiguration();
        pethorses.storage.yaml.YamlHorseDataRepository repo = new pethorses.storage.yaml.YamlHorseDataRepository(config);

        UUID[] ownerIds = {
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()
        };

        for (int i = 0; i < ownerIds.length; i++) {
            HorseData data = new HorseData();
            data.setOwnerId(ownerIds[i]);
            data.setLevel(i + 1);
            data.setHorseName("Horse" + i);

            assertDoesNotThrow(() -> repo.save(data));
        }

        for (int i = 0; i < ownerIds.length; i++) {
            HorseData loaded = repo.load(ownerIds[i]);
            assertEquals(i + 1, loaded.getLevel());
            assertEquals("Horse" + i, loaded.getHorseName());
        }
    }

    @Test
    public void testSaveNullHorseDataDoesNotThrow() {
        YamlConfiguration config = new YamlConfiguration();
        pethorses.storage.yaml.YamlHorseDataRepository repo = new pethorses.storage.yaml.YamlHorseDataRepository(config);

        assertDoesNotThrow(() -> repo.save(null));
    }

    @Test
    public void testSaveEmptyHorseDataWithoutOwnerIdIsIgnored() {
        YamlConfiguration config = new YamlConfiguration();
        pethorses.storage.yaml.YamlHorseDataRepository repo = new pethorses.storage.yaml.YamlHorseDataRepository(config);

        HorseData empty = new HorseData();
        repo.save(empty);

        assertNull(empty.getOwnerId());
        assertDoesNotThrow(() -> repo.save(empty));
    }
}

