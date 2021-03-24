package me.basiqueevangelist.nevseti;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

public interface OfflineDataChanged {
    Event<OfflineDataChanged> EVENT = EventFactory.createArrayBacked(OfflineDataChanged.class, callbacks -> (playerUuid, newTag) -> {
        for (OfflineDataChanged callback : callbacks) {
            callback.onOfflineDataChanged(playerUuid, newTag);
        }
    });

    void onOfflineDataChanged(UUID playerUuid, CompoundTag newTag);
}
