package me.basiqueevangelist.nevseti.advancements;

import me.basiqueevangelist.nevseti.mixin.AdvancementProgressAccessor;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.criterion.CriterionProgress;

import java.util.Arrays;
import java.util.Map;

class ImmutableAdvancementProgressWrapper implements AdvancementProgressView {
    private final AdvancementProgress progress;

    public ImmutableAdvancementProgressWrapper(AdvancementProgress progress) {
        this.progress = progress;
    }

    @Override
    public boolean isDone() {
        return progress.isDone();
    }

    @Override
    public boolean isAnyObtained() {
        return progress.isAnyObtained();
    }

    @Override
    public CriterionProgressView getCriterionProgress(String name) {
        return CriterionProgressView.take(progress.getCriterionProgress(name));
    }

    @Override
    public Iterable<String> getUnobtainedCriteria() {
        return progress.getUnobtainedCriteria();
    }

    @Override
    public Iterable<String> getObtainedCriteria() {
        return progress.getObtainedCriteria();
    }

    @Override
    public AdvancementProgress copy() {
        AdvancementProgress newProgress = new AdvancementProgress();
        String[][] newRequirements = Arrays.stream(((AdvancementProgressAccessor) progress).getRequirements()).map(String[]::clone).toArray(String[][]::new);
        ((AdvancementProgressAccessor) newProgress).setRequirements(newRequirements);
        Map<String, CriterionProgress> newCriteriaProgresses = ((AdvancementProgressAccessor) newProgress).getCriteriaProgresses();
        for (Map.Entry<String, CriterionProgress> entry : ((AdvancementProgressAccessor) progress).getCriteriaProgresses().entrySet()) {
            newCriteriaProgresses.put(entry.getKey(), ImmutableCriterionProgressWrapper.makeCopyOf(entry.getValue()));
        }
        return newProgress;
    }

    @Override
    public String toString() {
        return "ImmutableAdvancementProgressWrapper{" +
            "progress=" + progress +
            '}';
    }
}
