package pethorses.storage.yaml;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class YamlPassengerRepositoryTest {

    @Test
    public void testSaveAndLoad() {
        var config = new YamlConfiguration();
        var repo = new YamlPassengerRepository(config);
        UUID owner = UUID.randomUUID();
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();

        repo.save(owner, Set.of(p1, p2));

        var loaded = repo.load(owner);
        assertEquals(2, loaded.size());
        assertTrue(loaded.contains(p1));
        assertTrue(loaded.contains(p2));
    }

    @Test
    public void testLoadEmpty() {
        var config = new YamlConfiguration();
        var repo = new YamlPassengerRepository(config);
        UUID owner = UUID.randomUUID();

        var loaded = repo.load(owner);
        assertNotNull(loaded);
        assertTrue(loaded.isEmpty());
    }

    @Test
    public void testSaveSinglePassenger() {
        var config = new YamlConfiguration();
        var repo = new YamlPassengerRepository(config);
        UUID owner = UUID.randomUUID();
        UUID passenger = UUID.randomUUID();

        repo.save(owner, Set.of(passenger));

        var loaded = repo.load(owner);
        assertEquals(1, loaded.size());
        assertTrue(loaded.contains(passenger));
    }

    @Test
    public void testSaveMultiplePassengers() {
        var config = new YamlConfiguration();
        var repo = new YamlPassengerRepository(config);
        UUID owner = UUID.randomUUID();
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();
        UUID p3 = UUID.randomUUID();

        repo.save(owner, Set.of(p1, p2, p3));

        var loaded = repo.load(owner);
        assertEquals(3, loaded.size());
    }

    @Test
    public void testOverwritePreviousSave() {
        var config = new YamlConfiguration();
        var repo = new YamlPassengerRepository(config);
        UUID owner = UUID.randomUUID();
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();

        repo.save(owner, Set.of(p1));
        repo.save(owner, Set.of(p2));

        var loaded = repo.load(owner);
        assertEquals(1, loaded.size());
        assertTrue(loaded.contains(p2));
        assertFalse(loaded.contains(p1));
    }

    @Test
    public void testMultipleOwnersIndependent() {
        var config = new YamlConfiguration();
        var repo = new YamlPassengerRepository(config);
        UUID owner1 = UUID.randomUUID();
        UUID owner2 = UUID.randomUUID();
        UUID p1 = UUID.randomUUID();
        UUID p2 = UUID.randomUUID();

        repo.save(owner1, Set.of(p1));
        repo.save(owner2, Set.of(p2));

        var loaded1 = repo.load(owner1);
        var loaded2 = repo.load(owner2);

        assertEquals(1, loaded1.size());
        assertTrue(loaded1.contains(p1));
        assertFalse(loaded1.contains(p2));
        assertEquals(1, loaded2.size());
        assertTrue(loaded2.contains(p2));
        assertFalse(loaded2.contains(p1));
    }

    @Test
    public void testLoadInvalidUUID() {
        var config = new YamlConfiguration();
        var repo = new YamlPassengerRepository(config);
        UUID owner = UUID.randomUUID();
        config.set(owner.toString() + ".passengers", Set.of("invalid-uuid-string"));

        var loaded = repo.load(owner);
        assertTrue(loaded.isEmpty());
    }

    @Test
    public void testSaveEmptySet() {
        var config = new YamlConfiguration();
        var repo = new YamlPassengerRepository(config);
        UUID owner = UUID.randomUUID();

        repo.save(owner, Set.of());

        var loaded = repo.load(owner);
        assertTrue(loaded.isEmpty());
    }
}

