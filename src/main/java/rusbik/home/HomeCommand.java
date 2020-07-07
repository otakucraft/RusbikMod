package rusbik.home;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class HomeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("home").
                executes(context -> tpHome(context.getSource())));
    }

    private static int tpHome(ServerCommandSource source) throws CommandSyntaxException {
        HomeFileManager.tpHome(source.getPlayer());
        return 1;
    }
}
