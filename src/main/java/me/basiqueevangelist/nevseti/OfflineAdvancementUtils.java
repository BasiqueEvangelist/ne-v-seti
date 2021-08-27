package me.basiqueevangelist.nevseti;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.UUID;

public final class OfflineAdvancementUtils {
    private OfflineAdvancementUtils() {

    }

    public static AdvancementProgress getOrAddProgress(Map<Identifier, AdvancementProgress> map, Advancement advancement) {
        return map.computeIfAbsent(advancement.getId(), id -> {
            AdvancementProgress progress = new AdvancementProgress();
            progress.init(advancement.getCriteria(), advancement.getRequirements());
            return progress;
        });
    }
    
    public static void grant(UUID uuid, Advancement advancement) {
        Map<Identifier, AdvancementProgress> map = OfflineAdvancementLookup.get(uuid);
        AdvancementProgress progress = getOrAddProgress(map, advancement);
        for (String criterion : progress.getUnobtainedCriteria()) {
            progress.obtain(criterion);
        }
        OfflineAdvancementLookup.save(uuid, map);
    }
    
    public static void revoke(UUID uuid, Advancement advancement) {
        Map<Identifier, AdvancementProgress> map = OfflineAdvancementLookup.get(uuid);
        AdvancementProgress progress = getOrAddProgress(map, advancement);
        for (String criterion : progress.getObtainedCriteria()) {
            progress.reset(criterion);
        }
        OfflineAdvancementLookup.save(uuid, map);
    }
}
