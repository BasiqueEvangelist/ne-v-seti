package me.basiqueevangelist.nevseti;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import me.basiqueevangelist.nevseti.mixin.AdvancementProgressAccessor;
import net.minecraft.SharedConstants;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class OfflineAdvancementLookup {
    private OfflineAdvancementLookup() {

    }

    private final static Logger LOGGER = LogManager.getLogger("NeVSeti");
    private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer()).registerTypeAdapter(Identifier.class, new Identifier.Serializer()).setPrettyPrinting().create();
    private static final TypeToken<Map<Identifier, AdvancementProgress>> JSON_TYPE = new TypeToken<Map<Identifier, AdvancementProgress>>() {};

    private static void tryInitAdvancementProgress(Identifier advId, AdvancementProgress progress) {
        if (((AdvancementProgressAccessor) progress).getRequirements().length == 0) {
            Advancement adv = NeVSeti.currentServer.getAdvancementLoader().get(advId);

            if (adv != null) {
                ((AdvancementProgressAccessor) progress).setRequirements(adv.getRequirements());
            }
        }
    }

    public static void save(UUID player, Map<Identifier, AdvancementProgress> map) {
        PlayerAdvancementsSaved.EVENT.invoker().onPlayerAdvancementsSaved(player, map);

        try {
            Path advancementsPath = NeVSeti.currentServer.getSavePath(WorldSavePath.ADVANCEMENTS);
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

    public static Map<Identifier, AdvancementProgress> get(UUID player) {
        try {
            Path advancementsPath = NeVSeti.currentServer.getSavePath(WorldSavePath.ADVANCEMENTS);

            if (!Files.exists(advancementsPath))
                return null;

            Path advancementFile = advancementsPath.resolve(player + ".json");

            if (!Files.exists(advancementFile))
                return null;

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
            ImmutableMap.Builder<Identifier, AdvancementProgress> finalMap = ImmutableMap.builder();
            for (Map.Entry<Identifier, AdvancementProgress> entry : parsedMap.entrySet()) {
                tryInitAdvancementProgress(entry.getKey(), entry.getValue());
                finalMap.put(entry.getKey(), entry.getValue());
            }

            return finalMap.build();
        } catch (IOException e) {
            LOGGER.error("Couldn't get advancements for offline player {}", player, e);
            throw new RuntimeException(e);
        }
    }
}