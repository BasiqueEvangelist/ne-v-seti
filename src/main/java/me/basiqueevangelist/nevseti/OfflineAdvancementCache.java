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

public enum OfflineAdvancementCache {
    INSTANCE;

    private final static Logger LOGGER = LogManager.getLogger("NeVSeti");
    private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer()).registerTypeAdapter(Identifier.class, new Identifier.Serializer()).setPrettyPrinting().create();
    private static final TypeToken<Map<Identifier, AdvancementProgress>> JSON_TYPE = new TypeToken<Map<Identifier, AdvancementProgress>>() {};
    private final Map<UUID, Map<Identifier, AdvancementProgressView>> advancements = new HashMap<>();
    private MinecraftServer currentServer;

    void onServerStart(MinecraftServer server) {
        currentServer = server;

        try {
            Path advancementsPath = server.getSavePath(WorldSavePath.ADVANCEMENTS);

            if (Files.exists(advancementsPath)) {
                for (Path advancementFile : Files.list(advancementsPath).collect(Collectors.toList())) {
                    if (Files.isDirectory(advancementFile) || !advancementFile.toString().endsWith(".json")) {
                        continue;
                    }

                    try {
                        String filename = advancementFile.getFileName().toString();
                        String uuidStr = filename.substring(0, filename.lastIndexOf('.'));
                        UUID uuid = UUID.fromString(uuidStr);
                        Dynamic<JsonElement> dynamic;
                        try (InputStream s = Files.newInputStream(advancementFile);
                             InputStreamReader streamReader = new InputStreamReader(s);
                             JsonReader reader = new JsonReader(streamReader)) {
                            reader.setLenient(false);
                            dynamic = new Dynamic<>(JsonOps.INSTANCE, Streams.parse(reader));
                        }
                        if (!dynamic.get("DataVersion").asNumber().result().isPresent()) {
                            dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
                        }

                        dynamic = Schemas.getFixer().update(DataFixTypes.ADVANCEMENTS.getTypeReference(), dynamic, dynamic.get("DataVersion").asInt(0), SharedConstants.getGameVersion().getWorldVersion());
                        dynamic = dynamic.remove("DataVersion");

                        Map<Identifier, AdvancementProgress> parsedMap = GSON.getAdapter(JSON_TYPE).fromJsonTree(dynamic.getValue());
                        ImmutableMap.Builder<Identifier, AdvancementProgressView> finalMap = ImmutableMap.builder();
                        for (Map.Entry<Identifier, AdvancementProgress> entry : parsedMap.entrySet()) {
                            tryInitAdvancementProgress(entry.getKey(), entry.getValue());
                            finalMap.put(entry.getKey(), AdvancementProgressView.take(entry.getValue()));
                        }
                        advancements.put(uuid, finalMap.build());
                    } catch (CrashException | IOException | IllegalArgumentException | JsonSyntaxException e) {
                        LOGGER.error("Error while reading advancement file {}: {}", advancementFile, e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void onServerShutdown(MinecraftServer server) {
        currentServer = null;
    }

    private void tryInitAdvancementProgress(Identifier advId, AdvancementProgress progress) {
        if (((AdvancementProgressAccessor) progress).getRequirements().length == 0) {
            Advancement adv = currentServer.getAdvancementLoader().get(advId);

            if (adv != null) {
                ((AdvancementProgressAccessor) progress).setRequirements(adv.getRequirements());
            }
        }
    }

    /**
     * Sets the advancement data in the cache without saving to disk.
     */
    public Map<Identifier, AdvancementProgressView> set(UUID playerUuid, Map<Identifier, AdvancementProgress> map) {
        ImmutableMap.Builder<Identifier, AdvancementProgressView> finalMapBuilder = ImmutableMap.builder();
        for (Map.Entry<Identifier, AdvancementProgress> entry : map.entrySet()) {
            tryInitAdvancementProgress(entry.getKey(), entry.getValue());
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
        set(player, map);

        try {
            Path advancementsPath = currentServer.getSavePath(WorldSavePath.ADVANCEMENTS);
            Path advancementPath = advancementsPath.resolve(player.toString() + ".json");
            JsonElement savedElement = GSON.toJsonTree(map);
            savedElement.getAsJsonObject().addProperty("DataVersion", SharedConstants.getGameVersion().getWorldVersion());

            try (OutputStream os = Files.newOutputStream(advancementPath);
                 OutputStreamWriter osWriter = new OutputStreamWriter(os, Charsets.UTF_8.newEncoder())) {
                GSON.toJson(savedElement, osWriter);
            }

        } catch (IOException e) {
            LOGGER.error("Couldn't save advancements of offline player {}", player, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns an <b>unmodifiable</b> version of the advancement to progress map.
     * @see OfflineAdvancementUtils#copyAdvancementMap
     */
    public Map<Identifier, AdvancementProgressView> get(UUID player) {
        return advancements.get(player);
    }

    public Map<UUID, Map<Identifier, AdvancementProgressView>> getAdvancementData() {
        return Collections.unmodifiableMap(advancements);
    }
}
