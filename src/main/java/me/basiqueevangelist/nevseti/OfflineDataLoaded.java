package me.basiqueevangelist.nevseti;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface OfflineDataLoaded {
    /**
     * Triggered when all offline data caches are loaded.
     */
    Event<OfflineDataLoaded> EVENT = EventFactory.createArrayBacked(OfflineDataLoaded.class, handlers -> () -> {
        for (OfflineDataLoaded handler : handlers) {
            handler.onOfflineDataLoaded();
        }
    });

    void onOfflineDataLoaded();
}
