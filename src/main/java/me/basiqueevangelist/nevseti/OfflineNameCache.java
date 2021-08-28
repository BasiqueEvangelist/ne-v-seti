package me.basiqueevangelist.nevseti;

import com.google.common.collect.BiMap;
import me.basiqueevangelist.nevseti.api.v2.OfflineNameLookup;

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
