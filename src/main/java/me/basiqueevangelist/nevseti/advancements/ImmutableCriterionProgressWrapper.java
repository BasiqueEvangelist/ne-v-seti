package me.basiqueevangelist.nevseti.advancements;

import me.basiqueevangelist.nevseti.mixin.CriterionProgressAccessor;
import net.minecraft.advancement.criterion.CriterionProgress;

import java.util.Date;

class ImmutableCriterionProgressWrapper implements CriterionProgressView {
    private final CriterionProgress progress;

    public ImmutableCriterionProgressWrapper(CriterionProgress progress) {
        this.progress = progress;
    }

    @Override
    public boolean isObtained() {
        return progress.isObtained();
    }

    @Override
    public Date getObtainedDate() {
        return progress.getObtainedDate();
    }

    @Override
    public CriterionProgress copy() {
        return makeCopyOf(progress);
    }

    static CriterionProgress makeCopyOf(CriterionProgress progress) {
        CriterionProgress newProgress = new CriterionProgress();
        ((CriterionProgressAccessor) newProgress).setObtainedDate(progress.getObtainedDate());
        return newProgress;
    }

    @Override
    public String toString() {
        return "ImmutableCriterionProgressWrapper{" +
            "progress=" + progress +
            '}';
    }
}
