package me.basiqueevangelist.nevseti;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;

import java.util.Map;
import java.util.UUID;

public enum OfflineNameCache {
    INSTANCE;

    private final BiMap<UUID, String> names = HashBiMap.create();
    private MinecraftServer currentServer;

    OfflineNameCache() {
        OfflineDataChanged.EVENT.register((playerUuid, newTag) -> {
            if (newTag.contains("SavedUsername", NbtType.STRING)) {
                names.put(playerUuid, newTag.getString("SavedUsername"));
            }
        });
    }

    void onServerStart(MinecraftServer server) {
        currentServer = server;

        for (Map.Entry<UUID, CompoundTag> playerData : OfflineDataCache.INSTANCE.getPlayers().entrySet()) {
            if (playerData.getValue().contains("SavedUsername", NbtType.STRING)) {
                names.put(playerData.getKey(), playerData.getValue().getString());
            }
        }
    }

    void onServerShutdown(MinecraftServer server) {
        currentServer = null;
    }

    public void setInternal(UUID playerUuid, String name) {
        names.put(playerUuid, name);
    }

    public String getNameFromUUID(UUID playerUuid) {
        if (names.containsKey(playerUuid))
            return names.get(playerUuid);

        CompoundTag offlineData = OfflineDataCache.INSTANCE.get(playerUuid);
        if (offlineData != null && offlineData.contains("SavedUsername", NbtType.STRING)) {
            names.put(playerUuid, offlineData.getString("SavedUsername"));
        }

        GameProfile loadedProfile = currentServer.getUserCache().getByUuid(playerUuid);
        if (loadedProfile != null) {
            names.put(playerUuid, loadedProfile.getName());
            return loadedProfile.getName();
        }

        GameProfile profile = new GameProfile(playerUuid, null);
        currentServer.getSessionService().fillProfileProperties(profile, false);
        if (profile.isComplete()) {
            names.put(playerUuid, profile.getName());
            currentServer.getUserCache().add(profile);
            return profile.getName();
        }
        return null;
    }

    public UUID getUUIDFromName(String name) {
        if (names.containsValue(name)) {
            return names.inverse().get(name);
        }

        GameProfile loadedProfile = currentServer.getUserCache().findByName(name);
        if (loadedProfile != null) {
            names.put(loadedProfile.getId(), name);
            return loadedProfile.getId();
        }

        return null;
    }
}
