package me.basiqueevangelist.nevseti;

import me.basiqueevangelist.nevseti.api.OfflineDataLookup;
import me.basiqueevangelist.nevseti.api.PlayerDataSaved;
import me.basiqueevangelist.nevseti.nbt.NbtCompoundView;
import me.basiqueevangelist.nevseti.util.SignallingEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Deprecated(forRemoval = true)
public enum OfflineDataCache {
    INSTANCE;

    private final static Logger LOGGER = LogManager.getLogger("NeVSeti");
    private final Map<UUID, NbtCompoundView> savedPlayers = new HashMap<>();
    private boolean initted = false;

    static void register() {
        PlayerDataSaved.EVENT.register((playerUuid, newTag) -> {
            boolean eventSignalled = ((SignallingEvent<OfflineDataChanged>) OfflineDataChanged.EVENT).hasSignalled();

            if (!eventSignalled && !INSTANCE.initted) return;

            NbtCompoundView tag = NbtCompoundView.take(newTag);
            if (INSTANCE.initted)
                INSTANCE.savedPlayers.put(playerUuid, tag);

            if (eventSignalled)
                OfflineDataChanged.EVENT.invoker().onOfflineDataChanged(playerUuid, tag);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(INSTANCE::onServerShutdown);
    }

    void lazyInit() {
        if (initted) return;

        initted = true;

        for (UUID playerId : OfflineDataLookup.listSavedPlayers()) {
            NbtCompound tag = OfflineDataLookup.get(playerId);

            savedPlayers.put(playerId, NbtCompoundView.take(tag));
        }
    }

    void onServerShutdown(MinecraftServer server) {
        initted = false;
        savedPlayers.clear();
    }

    /**
     * Sets the player data tag in the cache without saving to disk.
     */
    public void set(UUID player, NbtCompound tag) {
        lazyInit();

        NbtCompoundView view = savedPlayers.put(player, NbtCompoundView.take(tag));
        
        OfflineDataChanged.EVENT.invoker().onOfflineDataChanged(player, view);
    }

    /**
     * Sets the player data tag in the cache and saves to disk.
     */
    public void save(UUID player, NbtCompound tag) {
        OfflineDataLookup.save(player, tag);
    }

    /**
     * Gets an unmodifiable version of the UUID to offline data tag map.
     */
    public Map<UUID, NbtCompoundView> getPlayers() {
        lazyInit();

        return Collections.unmodifiableMap(savedPlayers);
    }

    public NbtCompoundView get(UUID player) {
        lazyInit();

        return savedPlayers.get(player);
    }

    public NbtCompoundView reload(UUID player) {
        NbtCompoundView tag = NbtCompoundView.take(OfflineDataLookup.get(player));
        savedPlayers.put(player, tag);
        return tag;
    }
}
