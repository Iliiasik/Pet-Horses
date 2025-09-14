package nomadhorses.storage;

import nomadhorses.NomadHorses;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class YamlStorage implements StorageStrategy {
    private final NomadHorses plugin;
    private final Logger logger;
    private final File dataFile;
    private FileConfiguration dataConfig;

    private final Map<UUID, HorseData> horsesData = new HashMap<>();
    private final Map<UUID, Set<UUID>> passengerPermissions = new HashMap<>();

    public YamlStorage(NomadHorses plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.dataFile = new File(plugin.getDataFolder(), "horses_data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                logger.severe("Failed to create data file: " + e.getMessage());
            }
        }
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    @Override
    public void loadData() {
        for (String key : dataConfig.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(key);
                HorseData data = new HorseData();
                data.setOwnerId(playerId);
                data.setLevel(dataConfig.getInt(key + ".level", 1));
                data.setExperience(dataConfig.getInt(key + ".experience", 0));
                data.setColor(parseColor(dataConfig.getString(key + ".color", "BROWN")));
                data.setStyle(parseStyle(dataConfig.getString(key + ".style", "NONE")));
                data.setHorseName(dataConfig.getString(key + ".name"));
                data.setDeathTime(dataConfig.getLong(key + ".deathTime", 0));
                data.setJumps(dataConfig.getInt(key + ".jumps", 0));
                data.setBlocksTraveled(dataConfig.getDouble(key + ".blocksTraveled", 0.0));
                data.setTotalJumps(dataConfig.getInt(key + ".totalJumps", 0));
                data.setTotalBlocksTraveled(dataConfig.getDouble(key + ".totalBlocksTraveled", 0.0));

                if (dataConfig.contains(key + ".backpack")) {
                    List<?> backpackList = dataConfig.getList(key + ".backpack");
                    if (backpackList != null) {
                        ItemStack[] backpackItems = backpackList.toArray(new ItemStack[0]);
                        data.setBackpackItems(backpackItems);
                    }
                }

                if (dataConfig.contains(key + ".armor")) {
                    data.setArmorItem(dataConfig.getItemStack(key + ".armor"));
                }

                horsesData.put(playerId, data);

                if (dataConfig.contains(key + ".passengers")) {
                    Set<UUID> passengers = new HashSet<>();
                    for (String passengerId : dataConfig.getStringList(key + ".passengers")) {
                        try {
                            passengers.add(UUID.fromString(passengerId));
                        } catch (IllegalArgumentException e) {
                            logger.warning("Invalid passenger UUID: " + passengerId);
                        }
                    }
                    passengerPermissions.put(playerId, passengers);
                }
            } catch (IllegalArgumentException e) {
                logger.warning("Invalid UUID in data file: " + key);
            }
        }
    }

    @Override
    public HorseData getHorseData(UUID playerId) {
        return horsesData.computeIfAbsent(playerId, k -> new HorseData());
    }

    @Override
    public void saveHorseData(HorseData data) {
        String key = data.getOwnerId().toString();
        dataConfig.set(key + ".level", data.getLevel());
        dataConfig.set(key + ".experience", data.getExperience());
        dataConfig.set(key + ".color", data.getColor().name());
        dataConfig.set(key + ".style", data.getStyle().name());
        dataConfig.set(key + ".name", data.getHorseName());
        dataConfig.set(key + ".deathTime", data.getDeathTime());
        dataConfig.set(key + ".jumps", data.getJumps());
        dataConfig.set(key + ".blocksTraveled", data.getBlocksTraveled());
        dataConfig.set(key + ".totalJumps", data.getTotalJumps());
        dataConfig.set(key + ".totalBlocksTraveled", data.getTotalBlocksTraveled());

        if (data.getBackpackItems() != null && data.getBackpackItems().length > 0) {
            dataConfig.set(key + ".backpack", Arrays.asList(data.getBackpackItems()));
        } else {
            dataConfig.set(key + ".backpack", null);
        }

        dataConfig.set(key + ".armor", data.getArmorItem());

        if (passengerPermissions.containsKey(data.getOwnerId())) {
            List<String> passengers = passengerPermissions.get(data.getOwnerId()).stream()
                    .map(UUID::toString)
                    .collect(Collectors.toList());
            dataConfig.set(key + ".passengers", passengers);
        }

        saveToFile();
    }

    @Override
    public void saveAllData() {
        dataConfig.getKeys(false).forEach(key -> dataConfig.set(key, null));
        for (Map.Entry<UUID, HorseData> entry : horsesData.entrySet()) {
            String key = entry.getKey().toString();
            HorseData data = entry.getValue();
            dataConfig.set(key + ".level", data.getLevel());
            dataConfig.set(key + ".experience", data.getExperience());
            dataConfig.set(key + ".color", data.getColor().name());
            dataConfig.set(key + ".style", data.getStyle().name());
            dataConfig.set(key + ".name", data.getHorseName());
            dataConfig.set(key + ".deathTime", data.getDeathTime());
            dataConfig.set(key + ".jumps", data.getJumps());
            dataConfig.set(key + ".blocksTraveled", data.getBlocksTraveled());
            dataConfig.set(key + ".totalJumps", data.getTotalJumps());
            dataConfig.set(key + ".totalBlocksTraveled", data.getTotalBlocksTraveled());

            if (data.getBackpackItems() != null && data.getBackpackItems().length > 0) {
                dataConfig.set(key + ".backpack", Arrays.asList(data.getBackpackItems()));
            } else {
                dataConfig.set(key + ".backpack", null);
            }

            dataConfig.set(key + ".armor", data.getArmorItem());

            if (passengerPermissions.containsKey(entry.getKey())) {
                List<String> passengers = passengerPermissions.get(entry.getKey()).stream()
                        .map(UUID::toString)
                        .collect(Collectors.toList());
                dataConfig.set(key + ".passengers", passengers);
            }
        }
        saveToFile();
    }

    private void saveToFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            logger.severe("Failed to save horse data: " + e.getMessage());
        }
    }

    @Override
    public Set<UUID> getPassengers(UUID ownerUUID) {
        return passengerPermissions.getOrDefault(ownerUUID, Collections.emptySet());
    }

    @Override
    public void addPassenger(UUID ownerUUID, UUID passengerUUID) {
        passengerPermissions.computeIfAbsent(ownerUUID, k -> new HashSet<>()).add(passengerUUID);
    }

    @Override
    public void removePassenger(UUID ownerUUID, UUID passengerUUID) {
        passengerPermissions.getOrDefault(ownerUUID, Collections.emptySet()).remove(passengerUUID);
    }

    private Horse.Color parseColor(String colorStr) {
        try {
            return Horse.Color.valueOf(colorStr);
        } catch (IllegalArgumentException e) {
            return Horse.Color.BROWN;
        }
    }

    private Horse.Style parseStyle(String styleStr) {
        try {
            return Horse.Style.valueOf(styleStr);
        } catch (IllegalArgumentException e) {
            return Horse.Style.NONE;
        }
    }
}