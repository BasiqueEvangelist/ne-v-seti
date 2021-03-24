package me.basiqueevangelist.nevseti;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public enum OfflineDataCache {
    INSTANCE;

    private final static Logger LOGGER = LogManager.getLogger("NeVSeti");
    private final Map<UUID, CompoundTag> savedPlayers = new HashMap<>();
    private MinecraftServer currentServer;

    void onServerStart(MinecraftServer server) {
        currentServer = server;
        try {
            Path savedPlayersPath = server.getSavePath(WorldSavePath.PLAYERDATA);
            for (Path savedPlayerFile : Files.list(savedPlayersPath).collect(Collectors.toList())) {
                if (Files.isDirectory(savedPlayerFile) || !savedPlayerFile.toString().endsWith(".dat")) {
                    continue;
                }

                try {
                    CompoundTag tag = NbtIo.readCompressed(savedPlayerFile.toFile());
                    String filename = savedPlayerFile.getFileName().toString();
                    String uuidStr = filename.substring(0, filename.lastIndexOf('.'));
                    UUID uuid = UUID.fromString(uuidStr);
                    int dataVersion = tag.contains("DataVersion", 3) ? tag.getInt("DataVersion") : -1;
                    savedPlayers.put(uuid, NbtHelper.update(Schemas.getFixer(), DataFixTypes.PLAYER, tag, dataVersion));
                } catch (CrashException | IOException | IllegalArgumentException e) {
                    LOGGER.error("Error while reading playerdata file {}: {}", savedPlayerFile, e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void onServerShutdown(MinecraftServer server) {
        currentServer = null;
        savedPlayers.clear();
    }

    /**
     * Sets the player data tag in the cache without saving to disk.
     */
    public void set(UUID player, CompoundTag tag) {
        savedPlayers.put(player, tag);
        
        OfflineDataChanged.EVENT.invoker().onOfflineDataChanged(player, tag);
    }

    /**
     * Sets the player data tag in the cache and saves to disk.
     */
    public void save(UUID player, CompoundTag tag) {
        set(player, tag);

        try {
            File savedPlayersPath = currentServer.getSavePath(WorldSavePath.PLAYERDATA).toFile();
            File file = File.createTempFile(player.toString() + "-", ".dat", savedPlayersPath);
            NbtIo.writeCompressed(tag, file);
            File newDataFile = new File(savedPlayersPath, player.toString() + ".dat");
            File oldDataFile = new File(savedPlayersPath, player.toString() + ".dat_old");
            Util.backupAndReplace(newDataFile, file, oldDataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<UUID, CompoundTag> getPlayers() {
        return savedPlayers;
    }

    public CompoundTag get(UUID player) {
        return savedPlayers.get(player);
    }

    public CompoundTag reload(UUID player) {
        try {
            Path savedPlayersPath = currentServer.getSavePath(WorldSavePath.PLAYERDATA);
            Path savedDataPath = savedPlayersPath.resolve(player.toString() + ".dat");
            CompoundTag tag = NbtIo.readCompressed(savedDataPath.toFile());
            savedPlayers.put(player, tag);
            return tag;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
