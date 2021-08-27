package me.basiqueevangelist.nevseti;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import me.basiqueevangelist.nevseti.nbt.NbtCompoundView;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.server.MinecraftServer;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Deprecated(forRemoval = true)
public enum OfflineNameCache {
    INSTANCE;

    public void setInternal(UUID playerUuid, String name) {
        OfflineNameLookup.setInternal(playerUuid, name);
    }

    /**
     *  Gets an unmodifiable version of the UUID &lt;-&gt; username BiMap.
     */
    public BiMap<UUID, String> getNames() {
        return OfflineNameLookup.getNames();
    }

    public String getNameFromUUID(UUID playerUuid) {
        return OfflineNameLookup.getNameFromUUID(playerUuid);
    }

    public UUID getUUIDFromName(String name) {
        return OfflineNameLookup.getUUIDFromName(name);
    }
}
