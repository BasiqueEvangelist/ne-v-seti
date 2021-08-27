package me.basiqueevangelist.nevseti;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class OfflineDataLookup {
    private OfflineDataLookup() {

    }

    private final static Logger LOGGER = LogManager.getLogger("NeVSeti");

    public static void save(UUID player, NbtCompound tag) {
        PlayerDataSaved.EVENT.invoker().onPlayerDataSaved(player, tag);

        try {
            File savedPlayersPath = NeVSeti.currentServer.getSavePath(WorldSavePath.PLAYERDATA).toFile();
            File file = File.createTempFile(player.toString() + "-", ".dat", savedPlayersPath);
            NbtIo.writeCompressed(tag, file);
            File newDataFile = new File(savedPlayersPath, player.toString() + ".dat");
            File oldDataFile = new File(savedPlayersPath, player.toString() + ".dat_old");
            Util.backupAndReplace(newDataFile, file, oldDataFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static NbtCompound get(UUID player) {
        try {
            Path savedPlayersPath = NeVSeti.currentServer.getSavePath(WorldSavePath.PLAYERDATA);
            Path savedDataPath = savedPlayersPath.resolve(player.toString() + ".dat");
            return NbtIo.readCompressed(savedDataPath.toFile());
        } catch (IOException e) {
            LOGGER.error("Couldn't get player data for offline player {}", player, e);
            throw new RuntimeException(e);
        }
    }

    public static List<UUID> listSavedPlayers() {
        List<UUID> list = new ArrayList<>();

        try {
            Path savedPlayersPath = NeVSeti.currentServer.getSavePath(WorldSavePath.PLAYERDATA);
            Iterator<Path> iter = Files.list(savedPlayersPath).iterator();
            while(iter.hasNext()) {
                Path savedPlayerFile = iter.next();

                if (Files.isDirectory(savedPlayerFile) || !savedPlayerFile.toString().endsWith(".dat")) {
                    continue;
                }

                try {
                    String filename = savedPlayerFile.getFileName().toString();
                    String uuidStr = filename.substring(0, filename.lastIndexOf('.'));
                    UUID uuid = UUID.fromString(uuidStr);
                    list.add(uuid);
                } catch (IllegalArgumentException iae) {
                    LOGGER.error("Encountered invalid UUID in playerdata directory! ", iae);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
}
