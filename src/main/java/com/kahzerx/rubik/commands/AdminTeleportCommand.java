package com.kahzerx.rubik.commands;

import com.kahzerx.rubik.utils.KrusbibUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import java.util.Collection;

import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * TP para moderadores.
 */
public final class AdminTeleportCommand {
    private AdminTeleportCommand() { }

    /**
     * Registrar comando adminTp.
     * @param dispatcher usado para registrar el comando
     */
    public static void register(
            final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("adminTp").
                requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).
                then(CommandManager.argument("targets", EntityArgumentType.entities()).
                        then(CommandManager.argument("player", StringArgumentType.word()).
                                suggests((c, b) -> suggestMatching(KrusbibUtils.getPlayers(c.getSource()), b)).
                                executes(context -> tp(context.getSource(),
                                        EntityArgumentType.getEntities(context, "targets"),
                                        StringArgumentType.getString(context, "player")))).
                        then(argument("pos", BlockPosArgumentType.blockPos()).
                                executes(context -> tp(EntityArgumentType.getEntities(context, "targets"),
                                        BlockPosArgumentType.getBlockPos(context, "pos"))))).
                then(CommandManager.argument("player", StringArgumentType.word()).
                        suggests((c, b) -> suggestMatching(KrusbibUtils.getPlayers(c.getSource()), b)).
                        executes(context -> tp(context.getSource(),
                                StringArgumentType.getString(context, "player"))).
                        then(CommandManager.argument("player2", StringArgumentType.word()).
                                suggests((c, b) -> suggestMatching(KrusbibUtils.getPlayers(c.getSource()), b)).
                                executes(context -> tp(context.getSource(),
                                        StringArgumentType.getString(context, "player"),
                                        StringArgumentType.getString(context, "player2")))).
                        then(argument("pos", BlockPosArgumentType.blockPos()).
                                executes(context -> tp(context.getSource(),
                                        StringArgumentType.getString(context, "player"),
                                        BlockPosArgumentType.getBlockPos(context, "pos"))))).
                then(argument("coords", BlockPosArgumentType.blockPos()).
                        executes(context -> tp(context.getSource(),
                                BlockPosArgumentType.getBlockPos(context, "coords")))));
    }

    private static int tp(
            final ServerCommandSource source,
            final BlockPos pos) throws CommandSyntaxException {
        // De administrador a bloque.
        source.getPlayer().teleport(
                source.getWorld(),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                source.getPlayer().getYaw(),
                source.getPlayer().getPitch());
        return 1;
    }

    private static int tp(
            final ServerCommandSource source,
            final String player,
            final BlockPos pos) {
        // De jugador a bloque.
        ServerPlayerEntity playerEntity = source.
                getServer().
                getPlayerManager().
                getPlayer(player);
        if (playerEntity != null) {
            playerEntity.teleport(
                    source.getWorld(),
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    playerEntity.getYaw(),
                    playerEntity.getPitch());
        } else {
            source.sendFeedback(
                    new LiteralText(
                            "Este jugador no existe D:"),
                    false);
        }
        return 1;
    }

    private static int tp(
            final ServerCommandSource source,
            final String player,
            final String player2) throws CommandSyntaxException {
        // De jugador a jugador.
        ServerPlayerEntity playerEntity = source.
                getServer().
                getPlayerManager().
                getPlayer(player);
        ServerPlayerEntity playerEntity2 = source.
                getServer().
                getPlayerManager().
                getPlayer(player2);
        if (playerEntity != null && playerEntity2 != null) {
            // Si un admin se hace tp a un jugador en spectator,
            // es probablemente porque está en modo /c.
            if (playerEntity2.isSpectator()) {
                final int duration = 999999;
                source.getPlayer().changeGameMode(GameMode.SPECTATOR);
                source.getPlayer().addStatusEffect(
                        new StatusEffectInstance(
                                StatusEffects.NIGHT_VISION,
                                duration, 0,
                                false,
                                false));
                source.getPlayer().addStatusEffect(
                        new StatusEffectInstance(
                                StatusEffects.CONDUIT_POWER,
                                duration, 0,
                                false,
                                false));
            }
            playerEntity.teleport(
                    playerEntity2.getServerWorld(),
                    playerEntity2.getX(),
                    playerEntity2.getY(),
                    playerEntity2.getZ(),
                    playerEntity.getYaw(),
                    playerEntity.getPitch());
        } else {
            source.sendFeedback(
                    new LiteralText(
                            "Este jugador no existe D:"),
                    false);
        }
        return 1;
    }

    private static int tp(
            final ServerCommandSource source,
            final String player) throws CommandSyntaxException {
        // De administrador a jugador.
        ServerPlayerEntity playerEntity = source.
                getServer().
                getPlayerManager().
                getPlayer(player);
        if (playerEntity != null) {
            if (playerEntity.isSpectator()) {
                final int duration = 999999;
                source.getPlayer().changeGameMode(GameMode.SPECTATOR);
                source.getPlayer().addStatusEffect(
                        new StatusEffectInstance(
                                StatusEffects.NIGHT_VISION,
                                duration,
                                0,
                                false,
                                false));
                source.getPlayer().addStatusEffect(
                        new StatusEffectInstance(
                                StatusEffects.CONDUIT_POWER,
                                duration,
                                0,
                                false,
                                false));
                source.sendFeedback(
                        new LiteralText(
                                "Recuerda usar /s para volver a survival"),
                        false);
            }
            source.getPlayer().teleport(
                    playerEntity.getServerWorld(),
                    playerEntity.getX(),
                    playerEntity.getY(),
                    playerEntity.getZ(),
                    source.getPlayer().getYaw(),
                    source.getPlayer().getPitch());
        } else {
            source.sendFeedback(
                    new LiteralText(
                            "Este jugador no existe D:"),
                    false);
        }
        return 1;
    }

    private static int tp(final ServerCommandSource source,
                           final Collection<? extends Entity> targets,
                           final String player) {
        ServerPlayerEntity playerEntity = source.
                getServer().
                getPlayerManager().
                getPlayer(player);
        if (playerEntity != null) {
            for (Entity e : targets) {
                if (e instanceof ServerPlayerEntity players) {
                    players.teleport(
                            playerEntity.getServerWorld(),
                            playerEntity.getX(),
                            playerEntity.getY(),
                            playerEntity.getZ(),
                            players.getYaw(),
                            players.getPitch()
                    );

                } else {
                    e.teleport(
                            playerEntity.getX(),
                            playerEntity.getY(),
                            playerEntity.getZ()
                    );
                }
            }
        } else {
            source.sendFeedback(
                    new LiteralText(
                            "Este jugador no existe D:"),
                    false);
        }
        return 1;
    }

    private static int tp(final Collection<? extends Entity> targets, final BlockPos pos) {
        for (Entity e : targets) {
            e.teleport(pos.getX(), pos.getY(), pos.getZ());
        }
        return 1;
    }
}
