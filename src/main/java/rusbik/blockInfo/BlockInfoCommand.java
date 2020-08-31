package rusbik.blockInfo;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import rusbik.Rusbik;
import rusbik.database.RusbikDatabase;

import java.util.Collections;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BlockInfoCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("blockInfo").
                then(argument("coords", BlockPosArgumentType.blockPos()).
                        executes(context -> getInfo(context.getSource(), BlockPosArgumentType.getBlockPos(context, "coords")))));
    }

    public static int getInfo(ServerCommandSource source, BlockPos pos){
        try{
            List<String> msg = RusbikDatabase.getInfo(pos.getX(), pos.getY(), pos.getZ(), Rusbik.getDim(source.getWorld()));
            Collections.reverse(msg);
            source.sendFeedback(new LiteralText("======================"), false);
            for (String line : msg){
                source.sendFeedback(new LiteralText(line), false);
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return 1;
    }
}
