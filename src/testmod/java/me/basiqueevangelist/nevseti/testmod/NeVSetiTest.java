package me.basiqueevangelist.nevseti.testmod;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import me.basiqueevangelist.nevseti.*;
import me.basiqueevangelist.nevseti.advancements.AdvancementProgressView;
import me.basiqueevangelist.nevseti.api.v2.OfflineAdvancementLookup;
import me.basiqueevangelist.nevseti.api.v2.OfflineDataLookup;
import me.basiqueevangelist.nevseti.api.v2.OfflineNameLookup;
import me.basiqueevangelist.nevseti.nbt.NbtCompoundView;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
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
                            NbtCompound tag = OfflineDataLookup.get(profile.getId());
                            context.getSource().sendFeedback(NbtHelper.toPrettyPrintedText(tag), false);
                            return 0;
                        })));

            dispatcher.register(
                literal("testnamecache")
                    .then(argument("player", GameProfileArgumentType.gameProfile())
                        .executes(context -> {
                            GameProfile profile = GameProfileArgumentType.getProfileArgument(context, "player").iterator().next();
                            String name = OfflineNameLookup.getNameFromUUID(profile.getId());
                            context.getSource().sendFeedback(new LiteralText("Name: " + name), false);
                            String uuid = OfflineNameLookup.getUUIDFromName(profile.getName()).toString();
                            context.getSource().sendFeedback(new LiteralText("UUID: " + uuid), false);
                            return 0;
                        })));

            dispatcher.register(
                literal("testadvancementcache")
                    .then(literal("read")
                        .then(argument("player", GameProfileArgumentType.gameProfile())
                            .executes(context -> {
                                GameProfile profile = GameProfileArgumentType.getProfileArgument(context, "player").iterator().next();
                                Map<Identifier, AdvancementProgress> map = OfflineAdvancementLookup.get(profile.getId());
                                System.out.println(map);
                                return 0;
                            })))
                    .then(literal("write")
                        .then(argument("player", GameProfileArgumentType.gameProfile())
                            .executes(context -> {
                                MinecraftServer server = context.getSource().getServer();
                                GameProfile profile = GameProfileArgumentType.getProfileArgument(context, "player").iterator().next();
                                OfflineAdvancementUtils.grant(profile.getId(), server.getAdvancementLoader().get(new Identifier("story/iron_tools")));
                                return 0;
                            }))));

            addOldTests(dispatcher);
        });
    }

    @SuppressWarnings("removal")
    private void addOldTests(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("shownbt")
                .then(literal("old")
                    .then(argument("player", GameProfileArgumentType.gameProfile())
                        .executes(context -> {
                            GameProfile profile = GameProfileArgumentType.getProfileArgument(context, "player").iterator().next();
                            NbtCompoundView view = OfflineDataCache.INSTANCE.get(profile.getId());
                            context.getSource().sendFeedback(NbtHelper.toPrettyPrintedText(view.copy()), false);
                            return 0;
                        }))));

        dispatcher.register(
            literal("testnamecache")
                .then(literal("old")
                    .then(argument("player", GameProfileArgumentType.gameProfile())
                        .executes(context -> {
                            GameProfile profile = GameProfileArgumentType.getProfileArgument(context, "player").iterator().next();
                            String name = OfflineNameCache.INSTANCE.getNameFromUUID(profile.getId());
                            context.getSource().sendFeedback(new LiteralText("Name: " + name), false);
                            String uuid = OfflineNameCache.INSTANCE.getUUIDFromName(profile.getName()).toString();
                            context.getSource().sendFeedback(new LiteralText("UUID: " + uuid), false);
                            return 0;
                        }))));

        dispatcher.register(
            literal("testadvancementcache")
                .then(literal("old")
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
                                MinecraftServer server = context.getSource().getServer();
                                GameProfile profile = GameProfileArgumentType.getProfileArgument(context, "player").iterator().next();
                                OfflineAdvancementUtils.grant(profile.getId(), server.getAdvancementLoader().get(new Identifier("story/iron_tools")));
                                return 0;
                            })))));
    }
}
