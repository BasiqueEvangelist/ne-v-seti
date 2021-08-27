package me.basiqueevangelist.nevseti;

import me.basiqueevangelist.nevseti.api.OfflineNameLookup;
import me.basiqueevangelist.nevseti.mixin.AdvancementProgressAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

public class NeVSeti implements ModInitializer {
    public static MinecraftServer currentServer;

    @SuppressWarnings("removal")
    @Override
    public void onInitialize() {
        OfflineNameLookup.register();
        OfflineAdvancementCache.register();
        OfflineDataCache.register();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            currentServer = server;

            OfflineDataLoaded.EVENT.invoker().onOfflineDataLoaded();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            currentServer = null;
        });
    }

    @ApiStatus.Internal
    public static void tryInitAdvancementProgress(Identifier advId, AdvancementProgress progress) {
        if (((AdvancementProgressAccessor) progress).getRequirements().length == 0) {
            Advancement adv = NeVSeti.currentServer.getAdvancementLoader().get(advId);

            if (adv != null) {
                ((AdvancementProgressAccessor) progress).setRequirements(adv.getRequirements());
            }
        }
    }
}
