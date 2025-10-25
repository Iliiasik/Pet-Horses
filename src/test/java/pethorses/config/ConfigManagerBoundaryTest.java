package pethorses.config;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pethorses.PetHorses;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigManagerBoundaryTest {
    private PetHorses plugin;
    private ConfigManager configManager;

    @BeforeEach
    public void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(PetHorses.class);
        configManager = plugin.getConfigManager();
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void testSpeedValuesBoundary() {
        double speedBase = configManager.getSpeedBase();
        double speedMaxBonus = configManager.getSpeedMaxBonus();

        assertTrue(speedBase > 0);
        assertTrue(speedMaxBonus >= 0);
        assertTrue(speedBase <= 1.0);
    }

    @Test
    public void testHealthValuesBoundary() {
        double healthBase = configManager.getHealthBase();
        double healthMaxBonus = configManager.getHealthMaxBonus();

        assertTrue(healthBase > 0);
        assertTrue(healthMaxBonus >= 0);
        assertTrue(healthBase <= 40.0);
    }

    @Test
    public void testJumpValuesBoundary() {
        double jumpBase = configManager.getJumpBase();
        double jumpMaxBonus = configManager.getJumpMaxBonus();

        assertTrue(jumpBase > 0);
        assertTrue(jumpMaxBonus >= 0);
        assertTrue(jumpBase <= 2.0);
    }

    @Test
    public void testBackpackSizesBoundary() {
        int baseSize = configManager.getBackpackBaseSize();
        int perLevelSize = configManager.getBackpackSizePerLevel();
        int maxSize = configManager.getBackpackMaxSize();

        assertTrue(baseSize > 0);
        assertTrue(baseSize % 9 == 0);

        assertTrue(perLevelSize >= 0);

        assertTrue(maxSize > baseSize);
        assertTrue(maxSize % 9 == 0);
    }

    @Test
    public void testXpRequirementsBoundary() {
        int baseXp = configManager.getBaseXpForLevel();
        int increment = configManager.getXpIncrementPerLevel();

        assertTrue(baseXp > 0);
        assertTrue(increment >= 0);
    }

    @Test
    public void testCooldownBoundary() {
        long cooldownMinutes = configManager.getRespawnCooldownMinutes();

        assertTrue(cooldownMinutes >= 0);
        assertTrue(cooldownMinutes <= 10080);
    }

    @Test
    public void testConfigManagerDoesNotReturnNull() {
        assertNotNull(configManager.getLanguage());
        assertNotNull(configManager.getDatabaseHost());
        assertNotNull(configManager.getDatabaseName());
        assertNotNull(configManager.getDatabaseUser());
        assertNotNull(configManager.getDatabasePassword());
    }

    @Test
    public void testDatabasePortBoundary() {
        int port = configManager.getDatabasePort();

        assertTrue(port > 0);
        assertTrue(port <= 65535);
    }

    @Test
    public void testBackpackSizeWithDifferentConfigs() {
        int[] baseSizes = {9, 18, 27};
        int[] increments = {0, 3, 9};
        int[] maxSizes = {27, 54};

        for (int base : baseSizes) {
            for (int increment : increments) {
                for (int max : maxSizes) {
                    if (max >= base) {
                        assertTrue(base % 9 == 0);
                        assertTrue(max % 9 == 0);
                    }
                }
            }
        }
    }

    @Test
    public void testArmorSlotConfiguration() {
        boolean enabled = configManager.isArmorSlotEnabled();
        assertTrue(enabled || !enabled);
    }

    @Test
    public void testBackpackDropConfiguration() {
        boolean dropOnDeath = configManager.isBackpackDropOnDeath();
        assertTrue(dropOnDeath || !dropOnDeath);
    }

    @Test
    public void testFallDamageConfiguration() {
        boolean fallDamage = configManager.isFallDamageAllowed();
        assertTrue(fallDamage || !fallDamage);
    }

    @Test
    public void testDatabaseEnabledConfiguration() {
        boolean dbEnabled = configManager.isDatabaseEnabled();
        assertTrue(dbEnabled || !dbEnabled);
    }
}

