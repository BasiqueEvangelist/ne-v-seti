package me.basiqueevangelist.nevseti.mixin;

import com.mojang.authlib.GameProfile;
import me.basiqueevangelist.nevseti.OfflineNameCache;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onConstructed(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager, CallbackInfo cb) {
        if (profile.isComplete() && (Class<?>)getClass() == ServerPlayerEntity.class)
            OfflineNameCache.INSTANCE.setInternal(profile.getId(), profile.getName());
    }

    @Inject(method = "writeCustomDataToTag", at = @At("TAIL"))
    private void writeDataToTag(CompoundTag tag, CallbackInfo cb) {
        tag.putString("SavedUsername", getGameProfile().getName());
    }

}
