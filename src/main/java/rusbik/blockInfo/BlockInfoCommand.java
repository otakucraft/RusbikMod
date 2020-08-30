package rusbik.blockInfo;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BlockInfoCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("blockInfo").
                then(argument("coords", BlockPosArgumentType.blockPos()).
                        executes(context -> getInfo(context.getSource(), BlockPosArgumentType.getBlockPos(context, "coords")))));
    }

    public static int getInfo(ServerCommandSource source, BlockPos pos){
        System.out.println(pos);
        return 1;
    }
}
