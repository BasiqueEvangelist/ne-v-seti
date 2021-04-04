package me.basiqueevangelist.nevseti.mixin;

import me.basiqueevangelist.nevseti.OfflineAdvancementCache;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {
    @Shadow private ServerPlayerEntity owner;

    @Inject(method = "save", at = @At(value = "INVOKE", target = "Lcom/google/gson/Gson;toJsonTree(Ljava/lang/Object;)Lcom/google/gson/JsonElement;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onAdvancementsSaved(CallbackInfo ci, Map<Identifier, AdvancementProgress> map) {
        OfflineAdvancementCache.INSTANCE.set(owner.getUuid(), map);
    }
}
