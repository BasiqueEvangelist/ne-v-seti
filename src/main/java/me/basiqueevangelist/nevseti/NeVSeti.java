package me.basiqueevangelist.nevseti;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class NeVSeti implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            OfflineDataCache.INSTANCE.onServerStart(server);
            OfflineNameCache.INSTANCE.onServerStart(server);
            OfflineAdvancementCache.INSTANCE.onServerStart(server);

            OfflineDataLoaded.EVENT.invoker().onOfflineDataLoaded();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            OfflineDataCache.INSTANCE.onServerShutdown(server);
            OfflineNameCache.INSTANCE.onServerShutdown(server);
            OfflineAdvancementCache.INSTANCE.onServerShutdown(server);
        });
    }
}
