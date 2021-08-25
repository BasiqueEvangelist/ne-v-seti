package me.basiqueevangelist.nevseti.mixin;

import net.minecraft.advancement.criterion.CriterionProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Date;

@Mixin(CriterionProgress.class)
public interface CriterionProgressAccessor {
    @Accessor
    void setObtainedDate(Date date);
}
