package me.basiqueevangelist.nevseti;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;

public enum OfflineNameCache {
    INSTANCE;

    private final BiMap<UUID, String> names = HashBiMap.create();
    private MinecraftServer currentServer;

    void onServerStart(MinecraftServer server) {
        currentServer = server;
    }

    void onServerShutdown(MinecraftServer server) {
        currentServer = null;
    }

    public String getNameFromUUID(UUID playerUuid) {
        if (names.containsKey(playerUuid))
            return names.get(playerUuid);

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
