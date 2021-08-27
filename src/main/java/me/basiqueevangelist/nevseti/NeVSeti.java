package me.basiqueevangelist.nevseti;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class NeVSeti implements ModInitializer {
    public static MinecraftServer currentServer;

    @Override
    public void onInitialize() {
        OfflineNameLookup.register();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            currentServer = server;

            OfflineDataLookup.onServerStart(server);
            OfflineNameLookup.onServerStart(server);
            OfflineAdvancementLookup.onServerStart(server);

            OfflineDataLoaded.EVENT.invoker().onOfflineDataLoaded();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            OfflineDataLookup.onServerShutdown(server);

            currentServer = null;
        });
    }
}
