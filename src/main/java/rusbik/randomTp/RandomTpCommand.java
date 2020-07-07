package rusbik.randomTp;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
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
            BlockPos pos1 = source.getWorld().getTopPosition(Heightmap.Type.WORLD_SURFACE, player.getBlockPos());
            player.teleport(X, pos1.getY(), Z);
            ((ServerPlayerEntity) player).setSpawnPoint(World.OVERWORLD, player.getBlockPos(), true, false);
            ((ServerPlayerEntity) player).sendMessage(new LiteralText("Recuerda usar el /setHome"), false);
        }
        return 1;
    }
}