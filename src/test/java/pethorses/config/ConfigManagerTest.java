package pethorses.config;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pethorses.PetHorses;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigManagerTest {
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
    public void testGetLanguageDefault() {
        ConfigManager manager = plugin.getConfigManager();
        String language = manager.getLanguage();
        assertNotNull(language);
    }

    @Test
    public void testIsDatabaseEnabledDefault() {
        ConfigManager manager = plugin.getConfigManager();
        boolean enabled = manager.isDatabaseEnabled();
        assertFalse(enabled);
    }

    @Test
    public void testGetDatabaseHost() {
        ConfigManager manager = plugin.getConfigManager();
        String host = manager.getDatabaseHost();
        assertNotNull(host);
        assertNotEquals("", host);
    }

    @Test
    public void testGetDatabasePort() {
        ConfigManager manager = plugin.getConfigManager();
        int port = manager.getDatabasePort();
        assertTrue(port > 0);
    }

    @Test
    public void testGetDatabaseName() {
        ConfigManager manager = plugin.getConfigManager();
        String name = manager.getDatabaseName();
        assertNotNull(name);
    }

    @Test
    public void testGetDatabaseUser() {
        ConfigManager manager = plugin.getConfigManager();
        String user = manager.getDatabaseUser();
        assertNotNull(user);
    }

    @Test
    public void testGetDatabasePassword() {
        ConfigManager manager = plugin.getConfigManager();
        String password = manager.getDatabasePassword();
        assertNotNull(password);
    }

    @Test
    public void testGetBaseXpForLevel() {
        ConfigManager manager = plugin.getConfigManager();
        int xp = manager.getBaseXpForLevel();
        assertTrue(xp > 0);
    }

    @Test
    public void testGetXpIncrementPerLevel() {
        ConfigManager manager = plugin.getConfigManager();
        int increment = manager.getXpIncrementPerLevel();
        assertTrue(increment >= 0);
    }

    @Test
    public void testGetSpeedBase() {
        ConfigManager manager = plugin.getConfigManager();
        double speed = manager.getSpeedBase();
        assertTrue(speed > 0);
    }

    @Test
    public void testGetSpeedMaxBonus() {
        ConfigManager manager = plugin.getConfigManager();
        double bonus = manager.getSpeedMaxBonus();
        assertTrue(bonus >= 0);
    }

    @Test
    public void testGetHealthBase() {
        ConfigManager manager = plugin.getConfigManager();
        double health = manager.getHealthBase();
        assertTrue(health > 0);
    }

    @Test
    public void testGetHealthMaxBonus() {
        ConfigManager manager = plugin.getConfigManager();
        double bonus = manager.getHealthMaxBonus();
        assertTrue(bonus >= 0);
    }

    @Test
    public void testGetJumpBase() {
        ConfigManager manager = plugin.getConfigManager();
        double jump = manager.getJumpBase();
        assertTrue(jump > 0);
    }

    @Test
    public void testGetJumpMaxBonus() {
        ConfigManager manager = plugin.getConfigManager();
        double bonus = manager.getJumpMaxBonus();
        assertTrue(bonus >= 0);
    }

    @Test
    public void testGetRespawnCooldownMinutes() {
        ConfigManager manager = plugin.getConfigManager();
        long cooldown = manager.getRespawnCooldownMinutes();
        assertTrue(cooldown >= 0);
    }

    @Test
    public void testIsFallDamageAllowed() {
        ConfigManager manager = plugin.getConfigManager();
        boolean allowed = manager.isFallDamageAllowed();
        assertFalse(allowed);
    }

    @Test
    public void testGetBackpackBaseSize() {
        ConfigManager manager = plugin.getConfigManager();
        int size = manager.getBackpackBaseSize();
        assertTrue(size > 0);
    }

    @Test
    public void testGetBackpackSizePerLevel() {
        ConfigManager manager = plugin.getConfigManager();
        int size = manager.getBackpackSizePerLevel();
        assertTrue(size >= 0);
    }

    @Test
    public void testGetBackpackMaxSize() {
        ConfigManager manager = plugin.getConfigManager();
        int size = manager.getBackpackMaxSize();
        assertTrue(size > 0);
    }

    @Test
    public void testIsArmorSlotEnabled() {
        ConfigManager manager = plugin.getConfigManager();
        boolean enabled = manager.isArmorSlotEnabled();
        assertTrue(enabled);
    }

    @Test
    public void testIsBackpackDropOnDeath() {
        ConfigManager manager = plugin.getConfigManager();
        boolean drop = manager.isBackpackDropOnDeath();
        assertTrue(drop);
    }
}
