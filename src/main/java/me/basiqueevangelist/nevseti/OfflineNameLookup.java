package me.basiqueevangelist.nevseti;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;

import java.util.Optional;
import java.util.UUID;

public final class OfflineNameLookup {
    private OfflineNameLookup() {

    }

    private static final BiMap<UUID, String> names = HashBiMap.create();
    static void register() {
        PlayerDataSaved.EVENT.register((playerUuid, newTag) -> {
            if (newTag.contains("SavedUsername", NbtType.STRING)) {
                names.put(playerUuid, newTag.getString("SavedUsername"));
            }
        });
    }

    static void onServerStart(MinecraftServer server) {
        // FIXME: Make this work without a CompoundTag cache.
//        for (Map.Entry<UUID, NbtCompoundView> playerData : OfflineDataLookup.getPlayers().entrySet()) {
//            if (playerData.getValue().contains("SavedUsername", NbtType.STRING)) {
//                names.put(playerData.getKey(), playerData.getValue().getString("SavedUsername"));
//            }
//        }
    }

    public static void setInternal(UUID playerUuid, String name) {
        if (names.containsValue(name))
            names.inverse().remove(name);
        names.put(playerUuid, name);
    }

    /**
     *  Gets an unmodifiable version of the UUID &lt;-&gt; username BiMap.
     */
    public static BiMap<UUID, String> getNames() {
        return Maps.unmodifiableBiMap(names);
    }

    public static String getNameFromUUID(UUID playerUuid) {
        if (names.containsKey(playerUuid))
            return names.get(playerUuid);

        NbtCompound offlineData = OfflineDataLookup.get(playerUuid);
        if (offlineData != null && offlineData.contains("SavedUsername", NbtType.STRING)) {
            names.put(playerUuid, offlineData.getString("SavedUsername"));
        }

        Optional<GameProfile> loadedProfile = NeVSeti.currentServer.getUserCache().getByUuid(playerUuid);
        if (loadedProfile.isPresent()) {
            names.put(playerUuid, loadedProfile.get().getName());
            return loadedProfile.get().getName();
        }

        GameProfile profile = new GameProfile(playerUuid, null);
        NeVSeti.currentServer.getSessionService().fillProfileProperties(profile, false);
        if (profile.isComplete()) {
            names.put(playerUuid, profile.getName());
            NeVSeti.currentServer.getUserCache().add(profile);
            return profile.getName();
        }
        return null;
    }

    public static UUID getUUIDFromName(String name) {
        if (names.containsValue(name)) {
            return names.inverse().get(name);
        }

        Optional<GameProfile> loadedProfile = NeVSeti.currentServer.getUserCache().findByName(name);
        if (loadedProfile.isPresent()) {
            names.put(loadedProfile.get().getId(), name);
            return loadedProfile.get().getId();
        }

        return null;
    }
}
