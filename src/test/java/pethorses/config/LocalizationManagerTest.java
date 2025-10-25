package pethorses.config;

import org.mockbukkit.mockbukkit.MockBukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pethorses.PetHorses;

import static org.junit.jupiter.api.Assertions.*;

public class LocalizationManagerTest {
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
    public void testGetMessageReturnsString() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("plugin.enabled");
        assertNotNull(message);
    }

    @Test
    public void testGetMessageWithValidKey() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("help.title");
        assertNotNull(message);
        assertFalse(message.isEmpty());
    }

    @Test
    public void testGetMessageWithInvalidKeyReturnsMissingTranslation() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("invalid.key.that.does.not.exist");
        assertNotNull(message);
        assertTrue(message.contains("Missing translation") || message.contains("invalid.key"));
    }

    @Test
    public void testPluginEnabledMessage() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("plugin.enabled");
        assertNotNull(message);
    }

    @Test
    public void testPluginDisabledMessage() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("plugin.disabled");
        assertNotNull(message);
    }

    @Test
    public void testHorseHiddenMessage() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("horse.hidden");
        assertNotNull(message);
    }

    @Test
    public void testHorseFollowingMessage() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("horse.following");
        assertNotNull(message);
    }

    @Test
    public void testHorseStayingMessage() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("horse.staying");
        assertNotNull(message);
    }

    @Test
    public void testPassengerAllowedMessage() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("passenger.allowed");
        assertNotNull(message);
    }

    @Test
    public void testPassengerRemovedMessage() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("passenger.removed");
        assertNotNull(message);
    }

    @Test
    public void testPassengerListEmptyMessage() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("passenger.list_empty");
        assertNotNull(message);
    }

    @Test
    public void testErrorPlayerNotFoundMessage() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("error.player_not_found");
        assertNotNull(message);
    }

    @Test
    public void testErrorPlayersOnlyMessage() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("error.players_only");
        assertNotNull(message);
    }

    @Test
    public void testHelpTitleMessage() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("help.title");
        assertNotNull(message);
    }

    @Test
    public void testHelpStatsMessage() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("help.stats");
        assertNotNull(message);
    }

    @Test
    public void testHelpSummonMessage() {
        LocalizationManager manager = plugin.getLocalizationManager();
        String message = manager.getMessage("help.summon");
        assertNotNull(message);
    }
}

