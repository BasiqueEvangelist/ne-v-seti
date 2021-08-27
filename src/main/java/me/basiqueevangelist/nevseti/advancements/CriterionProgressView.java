package me.basiqueevangelist.nevseti.advancements;

import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.criterion.CriterionProgress;

import java.util.Date;

@Deprecated(forRemoval = true)
public interface CriterionProgressView {
    static CriterionProgressView take(CriterionProgress progress) { return new ImmutableCriterionProgressWrapper(progress); }

    boolean isObtained();
    Date getObtainedDate();

    CriterionProgress copy();
}
