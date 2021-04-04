package me.basiqueevangelist.nevseti;

import me.basiqueevangelist.nevseti.advancements.AdvancementProgressView;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public final class OfflineAdvancementUtils {
    private OfflineAdvancementUtils() {

    }

    /**
     * Creates a modifiable version of an advancement map.
     */
    public static Map<Identifier, AdvancementProgress> copyAdvancementMap(Map<Identifier, AdvancementProgressView> from) {
        Map<Identifier, AdvancementProgress> newMap = new HashMap<>();
        for (Map.Entry<Identifier, AdvancementProgressView> entry : from.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue().copy());
        }
        return newMap;
    }

    public static AdvancementProgress getProgress(Map<Identifier, AdvancementProgress> map, Advancement advancement) {
        return map.computeIfAbsent(advancement.getId(), id -> {
            AdvancementProgress progress = new AdvancementProgress();
            progress.init(advancement.getCriteria(), advancement.getRequirements());
            return progress;
        });
    }
}
