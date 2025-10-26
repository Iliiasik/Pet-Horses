package pethorses.storage.yaml;

import pethorses.PetHorses;
import pethorses.storage.HorseData;
import pethorses.storage.StorageStrategy;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class YamlStorage implements StorageStrategy {
    private final Logger logger;
    private final File dataFile;
    private final FileConfiguration dataConfig;
    private final YamlHorseDataRepository horseRepo;
    private final YamlPassengerRepository passengerRepo;
    private final Map<UUID, HorseData> horsesData;
    private final Map<UUID, Set<UUID>> passengerPermissions;
    private final Object fileLock = new Object();

    public YamlStorage(PetHorses plugin) {
        this.logger = plugin.getLogger();
        File candidate = new File(plugin.getDataFolder(), "horses_data.yml");
        File parent = candidate.getParentFile();
        boolean parentOk = false;
        if (parent != null) {
            if (parent.exists()) parentOk = true;
            else {
                try {
                    parentOk = parent.mkdirs();
                } catch (SecurityException ignored) {}
            }
        }
        if (!parentOk) {
            File alt = new File(System.getProperty("java.io.tmpdir"), "PetHorses-" + System.currentTimeMillis());
            try { if (alt.mkdirs()) parentOk = true; } catch (SecurityException ignored) {}
            if (parentOk) {
                dataFile = new File(alt, "horses_data.yml");
                logger.warning("Using system temp directory for horse data: " + dataFile.getAbsolutePath());
            } else {
                dataFile = candidate;
                logger.severe("Failed to create plugin data directory and temp fallback; writes may fail: " + candidate.getAbsolutePath());
            }
        } else {
            dataFile = candidate;
        }

        logger.fine("Horse data file path set to: " + dataFile.getAbsolutePath());

        if (!dataFile.exists()) {
            try {
                boolean created = dataFile.createNewFile();
                if (!created) logger.warning("Data file could not be created or already exists: " + dataFile.getAbsolutePath());
            } catch (IOException e) { logger.severe("Failed to create data file: " + e.getMessage()); }
        }

        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        this.horseRepo = new YamlHorseDataRepository(dataConfig);
        this.passengerRepo = new YamlPassengerRepository(dataConfig);
        this.horsesData = new ConcurrentHashMap<>();
        this.passengerPermissions = new ConcurrentHashMap<>();
    }

    @Override
    public void loadData() {
        horsesData.clear();
        passengerPermissions.clear();
        for (String key : dataConfig.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(key);
                horsesData.put(playerId, horseRepo.load(playerId));
                passengerPermissions.put(playerId, passengerRepo.load(playerId));
            } catch (IllegalArgumentException ignored) {}
        }
    }

    @Override
    public HorseData getHorseData(UUID playerId) {
        return horsesData.computeIfAbsent(playerId, k -> {
            HorseData hd = new HorseData();
            hd.setOwnerId(playerId);
            return hd;
        });
    }

    @Override
    public void saveHorseData(HorseData data) {
        if (data.getOwnerId() == null) {
            logger.warning("Attempted to save HorseData with null ownerId. Skipping save.");
            return;
        }
        synchronized (fileLock) {
            horseRepo.save(data);
            passengerRepo.save(data.getOwnerId(), passengerPermissions.getOrDefault(data.getOwnerId(), Collections.emptySet()));
            saveToFile();
        }
    }

    @Override
    public void saveAllData() {
        synchronized (fileLock) {
            for (String key : dataConfig.getKeys(false)) dataConfig.set(key, null);
            for (Map.Entry<UUID, HorseData> entry : horsesData.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null || entry.getValue().getOwnerId() == null) {
                    logger.warning("Skipping save for horse with null ownerId or HorseData.");
                    continue;
                }
                horseRepo.save(entry.getValue());
                passengerRepo.save(entry.getKey(), passengerPermissions.getOrDefault(entry.getKey(), Collections.emptySet()));
            }
            saveToFile();
        }
    }

    private void saveToFile() {
        Path targetPath = dataFile.toPath();
        Path parentPath = targetPath.getParent();

        int attempts = 0;
        while (attempts < 3) {
            try {
                if (parentPath != null) Files.createDirectories(parentPath);
                break;
            } catch (IOException e) {
                attempts++;
                logger.warning("Attempt " + attempts + " to create parent directories failed: " + e.getMessage());
                try { Thread.sleep(50); } catch (InterruptedException ignored) {}
            }
        }

        if (parentPath != null && !Files.exists(parentPath)) {
            logger.warning("Parent directory for horse data does not exist after retries: " + parentPath);
        }

        String content;
        try {
            content = dataConfig.saveToString();
        } catch (Throwable t) {
            logger.severe("Failed to serialize YAML content: " + t.getMessage());
            return;
        }

        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        Path tempPath = null;
        Exception lastTempEx = null;

        try {
            Path homeTemp = new File(System.getProperty("user.home"), ".pethorses_temp").toPath();
            try {
                Files.createDirectories(homeTemp);
                tempPath = Files.createTempFile(homeTemp, "horses_data-", ".tmp");
                Files.write(tempPath, bytes);
                logger.fine("Temp file created in user.home: " + tempPath);
            } catch (Exception e) {
                lastTempEx = e;
                logger.fine("Failed to write temp file in user.home: " + e.getMessage());
            }
        } catch (Exception e) {
            lastTempEx = e;
            logger.fine("Failed to prepare user.home temp dir: " + e.getMessage());
        }

        if (tempPath == null) {
            try {
                Path sysTmp = new File(System.getProperty("java.io.tmpdir")).toPath();
                Files.createDirectories(sysTmp);
                tempPath = Files.createTempFile(sysTmp, "horses_data-", ".tmp");
                Files.write(tempPath, bytes);
                logger.fine("Temp file created in system tmp: " + tempPath);
            } catch (Exception e) {
                lastTempEx = e;
                logger.warning("Writing temp file in java.io.tmpdir failed: " + e.getMessage());
            }
        }

        if (tempPath == null) {
            logger.severe("Unable to create any temp file for horse data; aborting save.");
            logger.severe("Last temp error: " + lastTempEx);
            return;
        }

        logger.fine("Prepared temp file: " + tempPath + ", target: " + targetPath);

        try {
            try { if (parentPath != null) Files.createDirectories(parentPath); } catch (IOException e) { logger.fine("Parent dirs create before move failed: " + e.getMessage()); }

            try {
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                logger.fine("Atomic move succeeded: " + targetPath);
                return;
            } catch (IOException atomicEx) {
                logger.fine("Atomic move failed, will try non-atomic: " + atomicEx.getMessage());
            }

            try {
                Files.move(tempPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                logger.fine("Non-atomic move succeeded: " + targetPath);
                return;
            } catch (IOException moveEx) {
                logger.warning("Non-atomic move failed: " + moveEx.getMessage());
            }

            try {
                Files.write(targetPath, bytes, java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.TRUNCATE_EXISTING);
                logger.fine("Wrote directly to target file after move failure: " + targetPath);
                try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
            } catch (IOException writeEx) {
                logger.severe("Direct write to target failed. temp=" + tempPath + " target=" + targetPath + " error=" + writeEx.getMessage());
                try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
            }
        } catch (Exception ex) {
            logger.severe("Unexpected error in saveToFile: " + ex.getMessage());
            try { Files.deleteIfExists(tempPath); } catch (IOException ignored) {}
        }
    }

    @Override
    public Set<UUID> getPassengers(UUID ownerUUID) {
        return passengerPermissions.getOrDefault(ownerUUID, Collections.emptySet());
    }

    @Override
    public void addPassenger(UUID ownerUUID, UUID passengerUUID) {
        passengerPermissions.computeIfAbsent(ownerUUID, k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(passengerUUID);
    }

    @Override
    public void removePassenger(UUID ownerUUID, UUID passengerUUID) {
        passengerPermissions.getOrDefault(ownerUUID, Collections.emptySet()).remove(passengerUUID);
    }

    public Set<UUID> getAllPlayerIds() {
        return horsesData.keySet();
    }
}
