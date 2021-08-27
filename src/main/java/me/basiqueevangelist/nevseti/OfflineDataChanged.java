package me.basiqueevangelist.nevseti;

import me.basiqueevangelist.nevseti.nbt.NbtCompoundView;
import me.basiqueevangelist.nevseti.util.SignallingEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.UUID;

@Deprecated(forRemoval = true)
public interface OfflineDataChanged {
    Event<OfflineDataChanged> EVENT = new SignallingEvent<>(EventFactory.createArrayBacked(OfflineDataChanged.class, callbacks -> (playerUuid, newTag) -> {
        for (OfflineDataChanged callback : callbacks) {
            callback.onOfflineDataChanged(playerUuid, newTag);
        }
    }));

    void onOfflineDataChanged(UUID playerUuid, NbtCompoundView newTag);
}
