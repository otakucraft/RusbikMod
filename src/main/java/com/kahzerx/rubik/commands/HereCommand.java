package com.kahzerx.rubik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import com.kahzerx.rubik.utils.KrusbibUtils;

import static net.minecraft.server.command.CommandManager.literal;

public final class HereCommand {
    /**
     * Comando /here para sacar las coordenadas del jugador que lo ejecuta,
     * saca la posición relativa en nether u overworld si fuera posible
     * y aplica 5 segundos de glowing.
     * @param dispatcher register command.
     */
    public static void register(
            final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("here").
                executes(context -> sendLocation(context.getSource())));
    }

    private HereCommand() { }

    /**
     * Sends player's coordinates.
     * @param source playerLocation.
     * @return Command success
     * @throws CommandSyntaxException DB connection.
     */

    public static int sendLocation(
            final ServerCommandSource source) throws CommandSyntaxException {
        final int dimension = 8;
        if (source.getPlayer() != null) {
            ServerPlayerEntity player = source.getPlayer();
            double x = source.getPlayer().getX();
            double y = source.getPlayer().getY();
            double z = source.getPlayer().getZ();

            if (player.
                    world.
                    getRegistryKey().
                    getValue().
                    equals(World.OVERWORLD.getValue())) {
                source.
                        getMinecraftServer().
                        getPlayerManager().
                        broadcastChatMessage(
                        new LiteralText(String.format(
                                "%s %s %s -> %s %s",
                                KrusbibUtils.getPlayerWithColor(player),
                                KrusbibUtils.getDimensionWithColor(player),
                                KrusbibUtils.formatCoords(x, y, z),
                                KrusbibUtils.getDimensionWithColor(
                                        World.NETHER.getValue()),
                                KrusbibUtils.formatCoords(
                                        x / dimension,
                                        y / dimension,
                                        z / dimension))),
                        MessageType.CHAT, Util.NIL_UUID);
            } else if (player.
                    world.
                    getRegistryKey().
                    getValue().
                    equals(World.NETHER.getValue())) {
                source.
                        getMinecraftServer().
                        getPlayerManager().
                        broadcastChatMessage(
                        new LiteralText(String.format(
                                "%s %s %s -> %s %s",
                                KrusbibUtils.getPlayerWithColor(player),
                                KrusbibUtils.getDimensionWithColor(player),
                                KrusbibUtils.formatCoords(x, y, z),
                                KrusbibUtils.getDimensionWithColor(
                                        World.OVERWORLD.getValue()),
                                KrusbibUtils.formatCoords(
                                        x * dimension,
                                        y * dimension,
                                        z * dimension))),
                        MessageType.CHAT, Util.NIL_UUID);
            } else if (player.
                    world.
                    getRegistryKey().
                    getValue().
                    equals(World.END.getValue())) {
                source.
                        getMinecraftServer().
                        getPlayerManager().
                        broadcastChatMessage(
                        new LiteralText(String.format(
                                "%s %s %s",
                                KrusbibUtils.getPlayerWithColor(player),
                                KrusbibUtils.getDimensionWithColor(player),
                                KrusbibUtils.formatCoords(x, y, z))),
                        MessageType.CHAT, Util.NIL_UUID);
            }
            final int duration = 100;
            player.addStatusEffect(
                    new StatusEffectInstance(StatusEffects.GLOWING,
                            duration,
                            0,
                            false,
                            false));
        }
        return 1;
    }
}
