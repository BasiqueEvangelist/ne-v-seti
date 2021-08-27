package me.basiqueevangelist.nevseti;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import me.basiqueevangelist.nevseti.advancements.AdvancementProgressView;
import me.basiqueevangelist.nevseti.mixin.AdvancementProgressAccessor;
import me.basiqueevangelist.nevseti.util.SignallingEvent;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.crash.CrashException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
public enum OfflineAdvancementCache {
    INSTANCE;

    private final static Logger LOGGER = LogManager.getLogger("NeVSeti");
    private final Map<UUID, Map<Identifier, AdvancementProgressView>> advancements = new HashMap<>();

    private boolean initted = false;

    static void register() {
        PlayerAdvancementsSaved.EVENT.register((playerUuid, newMap) -> {
            boolean eventSignalled = ((SignallingEvent<OfflineAdvancementsChanged>) OfflineAdvancementsChanged.EVENT).hasSignalled();

            if (!eventSignalled && !INSTANCE.initted) return;

            ImmutableMap.Builder<Identifier, AdvancementProgressView> finalMapBuilder = ImmutableMap.builder();
            for (Map.Entry<Identifier, AdvancementProgress> entry : newMap.entrySet()) {
                finalMapBuilder.put(entry.getKey(), AdvancementProgressView.take(entry.getValue()));
            }
            Map<Identifier, AdvancementProgressView> map = finalMapBuilder.build();
            if (INSTANCE.initted)
                INSTANCE.advancements.put(playerUuid, map);

            if (eventSignalled)
                OfflineAdvancementsChanged.EVENT.invoker().onOfflineAdvancementsChanged(playerUuid, map);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(INSTANCE::onServerShutdown);
    }

    void lazyInit() {
        if (initted) return;

        initted = true;

        for (UUID playerId : OfflineAdvancementLookup.listSavedPlayers()) {
            Map<Identifier, AdvancementProgress> map = OfflineAdvancementLookup.get(playerId);

            ImmutableMap.Builder<Identifier, AdvancementProgressView> finalMapBuilder = ImmutableMap.builder();
            for (Map.Entry<Identifier, AdvancementProgress> entry : map.entrySet()) {
                finalMapBuilder.put(entry.getKey(), AdvancementProgressView.take(entry.getValue()));
            }
            advancements.put(playerId, finalMapBuilder.build());
        }
    }

    void onServerShutdown(MinecraftServer server) {
        advancements.clear();
        initted = false;
    }

    /**
     * Sets the advancement data in the cache without saving to disk.
     */
    public Map<Identifier, AdvancementProgressView> set(UUID playerUuid, Map<Identifier, AdvancementProgress> map) {
        lazyInit();

        ImmutableMap.Builder<Identifier, AdvancementProgressView> finalMapBuilder = ImmutableMap.builder();
        for (Map.Entry<Identifier, AdvancementProgress> entry : map.entrySet()) {
            OfflineAdvancementLookup.tryInitAdvancementProgress(entry.getKey(), entry.getValue());
            finalMapBuilder.put(entry.getKey(), AdvancementProgressView.take(entry.getValue()));
        }
        Map<Identifier, AdvancementProgressView> finalMap = advancements.put(playerUuid, finalMapBuilder.build());

        OfflineAdvancementsChanged.EVENT.invoker().onOfflineAdvancementsChanged(playerUuid, finalMap);

        return finalMap;
    }

    /**
     * Sets the player data tag in the cache and saves to disk.
     */
    public void save(UUID player, Map<Identifier, AdvancementProgress> map) {
        OfflineAdvancementLookup.save(player, map);
    }

    /**
     * Returns an <b>unmodifiable</b> version of the advancement to progress map.
     * @see OfflineAdvancementUtils#copyAdvancementMap
     */
    public Map<Identifier, AdvancementProgressView> get(UUID player) {
        lazyInit();

        return advancements.get(player);
    }

    public Map<UUID, Map<Identifier, AdvancementProgressView>> getAdvancementData() {
        lazyInit();

        return Collections.unmodifiableMap(advancements);
    }
}
