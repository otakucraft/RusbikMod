package com.kahzerx.rubik.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Random;

import static net.minecraft.server.command.CommandManager.literal;

public final class RandomTpCommand {
    /**
     * Hacer tp al jugador a una posición random en un radio de 10k bloques.
     * @param dispatcher register command.
     */
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("randomCoords").
                requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).
                then(CommandManager.argument("player", EntityArgumentType.entity()).
                        executes(context -> tpAndSpawnPoint(
                                context.getSource(),
                                EntityArgumentType.getEntity(context, "player")))));
    }

    private RandomTpCommand() { }

    public static int tpAndSpawnPoint(final ServerCommandSource source, final Entity player) {
        if (player instanceof ServerPlayerEntity) {
            final int min = -10000;
            final int max = 10000;
            Random rand = new Random();
            double x = min + (max + max) * rand.nextDouble();
            double y = min + (max + max) * rand.nextDouble();
            final double z = 255;
            player.teleport(x, z, y);
            BlockPos pos1 = source.getWorld().getTopPosition(
                    Heightmap.Type.WORLD_SURFACE,
                    new BlockPos(x, z, y));
            // hacky way de conseguir el bloque más alto de la posición sobre la que te harás tp.
            BlockPos posBelow = new BlockPos(
                    pos1.getX(),
                    pos1.getY() - 1,
                    pos1.getZ());
            if (source.getWorld().getBlockState(posBelow).getBlock().equals(Blocks.WATER)
                    || source.getWorld().getBlockState(posBelow).getBlock().equals(Blocks.LAVA)) {
                tpAndSpawnPoint(source, player);  // Recursion de hacer tp si va a spawnear en agua o lava.
                // stackOverflow en mundos 100% agua? :D
            } else {
                player.teleport(x, pos1.getY(), y);
                ((ServerPlayerEntity) player).setSpawnPoint(
                        World.OVERWORLD,
                        player.getBlockPos(),
                        0.0F,
                        true,
                        false);
            }
        }
        return 1;
    }
}