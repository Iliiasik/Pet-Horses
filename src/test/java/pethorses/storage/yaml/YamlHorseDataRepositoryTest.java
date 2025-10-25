package pethorses.storage.yaml;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Horse;
import org.junit.jupiter.api.Test;
import pethorses.storage.HorseData;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class YamlHorseDataRepositoryTest {

    @Test
    public void testSaveAndLoad() {
        var config = new YamlConfiguration();
        var repo = new YamlHorseDataRepository(config);
        UUID id = UUID.randomUUID();
        HorseData data = new HorseData();
        data.setOwnerId(id);
        data.setLevel(5);
        data.setExperience(150);
        data.setHorseName("TestHorse");

        repo.save(data);

        HorseData loaded = repo.load(id);
        assertEquals(5, loaded.getLevel());
        assertEquals(150, loaded.getExperience());
        assertEquals("TestHorse", loaded.getHorseName());
    }

    @Test
    public void testLoadWithDefaultValues() {
        var config = new YamlConfiguration();
        var repo = new YamlHorseDataRepository(config);
        UUID id = UUID.randomUUID();

        HorseData loaded = repo.load(id);
        assertEquals(1, loaded.getLevel());
        assertEquals(0, loaded.getExperience());
        assertEquals(Horse.Color.BROWN, loaded.getColor());
        assertEquals(Horse.Style.NONE, loaded.getStyle());
    }

    @Test
    public void testSaveColor() {
        var config = new YamlConfiguration();
        var repo = new YamlHorseDataRepository(config);
        UUID id = UUID.randomUUID();
        HorseData data = new HorseData();
        data.setOwnerId(id);
        data.setColor(Horse.Color.CHESTNUT);

        repo.save(data);

        HorseData loaded = repo.load(id);
        assertEquals(Horse.Color.CHESTNUT, loaded.getColor());
    }

    @Test
    public void testSaveStyle() {
        var config = new YamlConfiguration();
        var repo = new YamlHorseDataRepository(config);
        UUID id = UUID.randomUUID();
        HorseData data = new HorseData();
        data.setOwnerId(id);
        data.setStyle(Horse.Style.WHITE);

        repo.save(data);

        HorseData loaded = repo.load(id);
        assertEquals(Horse.Style.WHITE, loaded.getStyle());
    }

    @Test
    public void testSaveInvalidColorParsesAsDefault() {
        var config = new YamlConfiguration();
        var repo = new YamlHorseDataRepository(config);
        UUID id = UUID.randomUUID();
        config.set(id.toString() + ".color", "INVALID_COLOR");

        HorseData loaded = repo.load(id);
        assertEquals(Horse.Color.BROWN, loaded.getColor());
    }

    @Test
    public void testSaveInvalidStyleParsesAsDefault() {
        var config = new YamlConfiguration();
        var repo = new YamlHorseDataRepository(config);
        UUID id = UUID.randomUUID();
        config.set(id.toString() + ".style", "INVALID_STYLE");

        HorseData loaded = repo.load(id);
        assertEquals(Horse.Style.NONE, loaded.getStyle());
    }

    @Test
    public void testSaveWithNullOwnerId() {
        var config = new YamlConfiguration();
        var repo = new YamlHorseDataRepository(config);
        HorseData data = new HorseData();
        data.setOwnerId(null);

        repo.save(data);
    }

    @Test
    public void testSaveStats() {
        var config = new YamlConfiguration();
        var repo = new YamlHorseDataRepository(config);
        UUID id = UUID.randomUUID();
        HorseData data = new HorseData();
        data.setOwnerId(id);
        data.setJumps(5);
        data.setBlocksTraveled(25.5);
        data.setTotalJumps(50);
        data.setTotalBlocksTraveled(200.3);

        repo.save(data);

        HorseData loaded = repo.load(id);
        assertEquals(5, loaded.getJumps());
        assertEquals(25.5, loaded.getBlocksTraveled(), 0.01);
        assertEquals(50, loaded.getTotalJumps());
        assertEquals(200.3, loaded.getTotalBlocksTraveled(), 0.01);
    }

    @Test
    public void testSaveDeathTime() {
        var config = new YamlConfiguration();
        var repo = new YamlHorseDataRepository(config);
        UUID id = UUID.randomUUID();
        HorseData data = new HorseData();
        data.setOwnerId(id);
        long deathTime = System.currentTimeMillis();
        data.setDeathTime(deathTime);

        repo.save(data);

        HorseData loaded = repo.load(id);
        assertEquals(deathTime, loaded.getDeathTime());
    }

    @Test
    public void testMultipleHorsesIndependent() {
        var config = new YamlConfiguration();
        var repo = new YamlHorseDataRepository(config);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        HorseData data1 = new HorseData();
        data1.setOwnerId(id1);
        data1.setLevel(1);

        HorseData data2 = new HorseData();
        data2.setOwnerId(id2);
        data2.setLevel(10);

        repo.save(data1);
        repo.save(data2);

        HorseData loaded1 = repo.load(id1);
        HorseData loaded2 = repo.load(id2);

        assertEquals(1, loaded1.getLevel());
        assertEquals(10, loaded2.getLevel());
    }
}

