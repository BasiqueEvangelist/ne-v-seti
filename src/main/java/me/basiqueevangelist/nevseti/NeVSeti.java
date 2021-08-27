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

            OfflineNameLookup.onServerStart(server);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {

            currentServer = null;
        });
    }
}
