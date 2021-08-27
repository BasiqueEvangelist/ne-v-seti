package me.basiqueevangelist.nevseti;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class NeVSeti implements ModInitializer {
    @Override
    public void onInitialize() {
        OfflineNameCache.register();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            OfflineDataCache.onServerStart(server);
            OfflineNameCache.onServerStart(server);
            OfflineAdvancementCache.onServerStart(server);

            OfflineDataLoaded.EVENT.invoker().onOfflineDataLoaded();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            OfflineDataCache.onServerShutdown(server);
            OfflineNameCache.onServerShutdown(server);
            OfflineAdvancementCache.onServerShutdown(server);
        });
    }
}
