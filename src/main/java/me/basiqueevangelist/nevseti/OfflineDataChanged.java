package me.basiqueevangelist.nevseti;

import me.basiqueevangelist.nevseti.nbt.NbtCompoundView;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.UUID;

public interface OfflineDataChanged {
    Event<OfflineDataChanged> EVENT = EventFactory.createArrayBacked(OfflineDataChanged.class, callbacks -> (playerUuid, newTag) -> {
        for (OfflineDataChanged callback : callbacks) {
            callback.onOfflineDataChanged(playerUuid, newTag);
        }
    });

    void onOfflineDataChanged(UUID playerUuid, NbtCompoundView newTag);
}
