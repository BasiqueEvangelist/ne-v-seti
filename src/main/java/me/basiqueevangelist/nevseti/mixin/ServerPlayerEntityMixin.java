package me.basiqueevangelist.nevseti.mixin;

import com.mojang.authlib.GameProfile;
import me.basiqueevangelist.nevseti.api.OfflineNameLookup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
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
    private void onConstructed(MinecraftServer server, ServerWorld world, GameProfile profile, CallbackInfo ci) {
        // *Maybe* fix issues with fake player adding mods.
        if (profile.isComplete() && (Class<?>)getClass() == ServerPlayerEntity.class)
            OfflineNameLookup.setInternal(profile.getId(), profile.getName());
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeDataToTag(NbtCompound tag, CallbackInfo cb) {
        tag.putString("SavedUsername", getGameProfile().getName());
    }

}
