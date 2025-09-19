package pethorses.listeners;

import pethorses.PetHorses;
import pethorses.config.LocalizationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatInputListener implements Listener {
    private final Map<UUID, Consumer<String>> inputCallbacks = new HashMap<>();
    private final PetHorses plugin;
    private final LocalizationManager localizationManager;

    public ChatInputListener(PetHorses plugin) {
        this.plugin = plugin;
        this.localizationManager = plugin.getLocalizationManager();
    }

    public void awaitInput(Player player, Consumer<String> callback) {
        inputCallbacks.put(player.getUniqueId(), callback);
        player.sendMessage(localizationManager.getMessage("input.prompt"));
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!inputCallbacks.containsKey(player.getUniqueId())) return;

        event.setCancelled(true);
        String message = event.getMessage();

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (message.equalsIgnoreCase(localizationManager.getMessage("input.cancel_keyword"))) {
                player.sendMessage(localizationManager.getMessage("input.cancelled"));
            } else {
                inputCallbacks.get(player.getUniqueId()).accept(message);
            }
            inputCallbacks.remove(player.getUniqueId());
        });
    }
}