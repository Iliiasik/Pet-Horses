package pethorses.storage.yaml;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.*;
import java.util.stream.Collectors;

public class YamlPassengerRepository {
    private final FileConfiguration config;

    public YamlPassengerRepository(FileConfiguration config) {
        this.config = config;
    }

    public Set<UUID> load(UUID ownerId) {
        String key = ownerId.toString();
        Set<UUID> passengers = new HashSet<>();
        if (config.contains(key + ".passengers")) {
            for (String passengerId : config.getStringList(key + ".passengers")) {
                try {
                    passengers.add(UUID.fromString(passengerId));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return passengers;
    }

    public void save(UUID ownerId, Set<UUID> passengers) {
        String key = ownerId.toString();
        List<String> list = passengers.stream().map(UUID::toString).collect(Collectors.toList());
        config.set(key + ".passengers", list);
    }
}
