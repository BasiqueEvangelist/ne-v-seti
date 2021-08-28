package me.basiqueevangelist.nevseti;

import me.basiqueevangelist.nevseti.advancements.AdvancementProgressView;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
public final class OfflineAdvancementUtils {
    private OfflineAdvancementUtils() {

    }

    public static Map<Identifier, AdvancementProgress> copyAdvancementMap(Map<Identifier, AdvancementProgressView> from) {
        Map<Identifier, AdvancementProgress> newMap = new HashMap<>();
        for (Map.Entry<Identifier, AdvancementProgressView> entry : from.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().copy());
        }
        return newMap;
    }

    public static AdvancementProgress getOrAddProgress(Map<Identifier, AdvancementProgress> map, Advancement advancement) {
        return me.basiqueevangelist.nevseti.api.v2.OfflineAdvancementUtils.getOrAddProgress(map, advancement);
    }
    
    public static void grant(UUID uuid, Advancement advancement) {
        me.basiqueevangelist.nevseti.api.v2.OfflineAdvancementUtils.grant(uuid, advancement);
    }
    
    public static void revoke(UUID uuid, Advancement advancement) {
        me.basiqueevangelist.nevseti.api.v2.OfflineAdvancementUtils.revoke(uuid, advancement);
    }
}
