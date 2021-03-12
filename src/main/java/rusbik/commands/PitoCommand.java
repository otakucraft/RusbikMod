package rusbik.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import static net.minecraft.server.command.CommandManager.literal;

public class PitoCommand {
    //  ...
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("pito").
                executes(context -> msg(context.getSource())));
    }
    public static int msg(ServerCommandSource source){
        source.sendFeedback(new LiteralText("Buena tula mi rey."), false);
        return 1;
    }
}
