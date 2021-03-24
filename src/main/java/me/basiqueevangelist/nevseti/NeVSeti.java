package me.basiqueevangelist.nevseti;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class NeVSeti implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(OfflineDataCache.INSTANCE::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPED.register(OfflineDataCache.INSTANCE::onServerShutdown);

        ServerLifecycleEvents.SERVER_STARTED.register(OfflineNameCache.INSTANCE::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPED.register(OfflineNameCache.INSTANCE::onServerShutdown);
    }
}
