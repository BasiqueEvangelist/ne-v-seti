package me.basiqueevangelist.nevseti.advancements;

import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.criterion.CriterionProgress;

public interface AdvancementProgressView {
    static AdvancementProgressView take(AdvancementProgress progress) { return new ImmutableAdvancementProgressWrapper(progress); }

    boolean isDone();
    boolean isAnyObtained();
    CriterionProgressView getCriterionProgress(String name);
    Iterable<String> getUnobtainedCriteria();
    Iterable<String> getObtainedCriteria();

    AdvancementProgress copy();
}
