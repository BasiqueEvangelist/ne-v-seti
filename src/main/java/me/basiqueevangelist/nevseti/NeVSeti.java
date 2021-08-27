package me.basiqueevangelist.nevseti;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class NeVSeti implements ModInitializer {
    public static MinecraftServer currentServer;

    @Override
    public void onInitialize() {
        OfflineNameCache.register();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            currentServer = server;

            OfflineDataCache.onServerStart(server);
            OfflineNameCache.onServerStart(server);
            OfflineAdvancementCache.onServerStart(server);

            OfflineDataLoaded.EVENT.invoker().onOfflineDataLoaded();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            OfflineDataCache.onServerShutdown(server);

            currentServer = null;
        });
    }
}
