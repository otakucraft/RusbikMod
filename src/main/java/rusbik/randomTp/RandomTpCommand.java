package rusbik.randomTp;

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

public class RandomTpCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("randomCoords").
                requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).
                then(CommandManager.argument("player", EntityArgumentType.entity()).
                        executes(context -> tpAndSpawnPoint(context.getSource(), EntityArgumentType.getEntity(context, "player")))));
    }

    public static int tpAndSpawnPoint(ServerCommandSource source, Entity player) {
        if (player instanceof ServerPlayerEntity){
            Random rand = new Random();
            double X = -10000 + (10000 + 10000) * rand.nextDouble();
            double Z = -10000 + (10000 + 10000) * rand.nextDouble();
            double Y = 255;
            player.teleport(X, Y, Z);
            BlockPos pos1 = source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(X, Y, Z));
            BlockPos posBelow = new BlockPos(pos1.getX(), pos1.getY() - 1, pos1.getZ());
            if (source.getWorld().getBlockState(posBelow).getBlock().equals(Blocks.WATER) || source.getWorld().getBlockState(posBelow).getBlock().equals(Blocks.LAVA)){
                tpAndSpawnPoint(source, player);
            }
            else{
                player.teleport(X, pos1.getY(), Z);
                ((ServerPlayerEntity) player).setSpawnPoint(World.OVERWORLD, player.getBlockPos(), 0.0F, true, false);
            }
        }
        return 1;
    }
}