package me.basiqueevangelist.nevseti;

import me.basiqueevangelist.nevseti.advancements.AdvancementProgressView;
import me.basiqueevangelist.nevseti.util.SignallingEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
public interface OfflineAdvancementsChanged {
    Event<OfflineAdvancementsChanged> EVENT = new SignallingEvent<>(EventFactory.createArrayBacked(OfflineAdvancementsChanged.class, callbacks -> (playerUuid, newMap) -> {
        for (OfflineAdvancementsChanged callback : callbacks) {
            callback.onOfflineAdvancementsChanged(playerUuid, newMap);
        }
    }));

    void onOfflineAdvancementsChanged(UUID playerUuid, Map<Identifier, AdvancementProgressView> newMap);
}
