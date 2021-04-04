package me.basiqueevangelist.nevseti.testmod;

import com.mojang.authlib.GameProfile;
import me.basiqueevangelist.nevseti.OfflineAdvancementCache;
import me.basiqueevangelist.nevseti.OfflineAdvancementUtils;
import me.basiqueevangelist.nevseti.OfflineDataCache;
import me.basiqueevangelist.nevseti.OfflineNameCache;
import me.basiqueevangelist.nevseti.advancements.AdvancementProgressView;
import me.basiqueevangelist.nevseti.nbt.CompoundTagView;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.util.Map;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class NeVSetiTest implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
                literal("shownbt")
                    .then(argument("player", GameProfileArgumentType.gameProfile())
                        .executes(context -> {
                            GameProfile profile = GameProfileArgumentType.getProfileArgument(context, "player").iterator().next();
                            CompoundTagView view = OfflineDataCache.INSTANCE.get(profile.getId());
                            context.getSource().sendFeedback(view.copy().toText(), false);
                            return 0;
                        })));

            dispatcher.register(
                literal("testnamecache")
                    .then(argument("player", GameProfileArgumentType.gameProfile())
                        .executes(context -> {
                            GameProfile profile = GameProfileArgumentType.getProfileArgument(context, "player").iterator().next();
                            String name = OfflineNameCache.INSTANCE.getNameFromUUID(profile.getId());
                            context.getSource().sendFeedback(new LiteralText("Name: " + name), false);
                            String uuid = OfflineNameCache.INSTANCE.getUUIDFromName(profile.getName()).toString();
                            context.getSource().sendFeedback(new LiteralText("UUID: " + uuid), false);
                            return 0;
                        })));

            dispatcher.register(
                literal("testadvancementcache")
                    .then(literal("read")
                        .then(argument("player", GameProfileArgumentType.gameProfile())
                            .executes(context -> {
                                GameProfile profile = GameProfileArgumentType.getProfileArgument(context, "player").iterator().next();
                                Map<Identifier, AdvancementProgressView> map = OfflineAdvancementCache.INSTANCE.get(profile.getId());
                                System.out.println(map);
                                return 0;
                            })))
                    .then(literal("write")
                        .then(argument("player", GameProfileArgumentType.gameProfile())
                            .executes(context -> {
                                MinecraftServer server = context.getSource().getMinecraftServer();
                                GameProfile profile = GameProfileArgumentType.getProfileArgument(context, "player").iterator().next();
                                OfflineAdvancementUtils.grant(profile.getId(), server.getAdvancementLoader().get(new Identifier("story/iron_tools")));
                                return 0;
                            }))));
        });
    }
}
