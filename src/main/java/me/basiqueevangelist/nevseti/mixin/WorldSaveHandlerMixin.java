package me.basiqueevangelist.nevseti.mixin;

import me.basiqueevangelist.nevseti.OfflineDataCache;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.WorldSaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WorldSaveHandler.class)
public class WorldSaveHandlerMixin {
	@Inject(method = "savePlayerData", at = @At(value = "INVOKE", target = "Ljava/io/File;createTempFile(Ljava/lang/String;Ljava/lang/String;Ljava/io/File;)Ljava/io/File;"), locals = LocalCapture.CAPTURE_FAILHARD)
	public void onPlayerDataSaved(PlayerEntity player, CallbackInfo ci, NbtCompound tag) {
		OfflineDataCache.INSTANCE.set(player.getUuid(), tag);
	}
}
