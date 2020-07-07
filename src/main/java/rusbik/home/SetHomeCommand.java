package rusbik.home;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class SetHomeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("setHome").
                executes(context -> setHome(context.getSource())));
    }

    public static int setHome(ServerCommandSource source) throws CommandSyntaxException {
        HomeFileManager.setHome(source.getPlayer(),source.getWorld(), source.getPlayer().getPos().x, source.getPlayer().getPos().y, source.getPlayer().getPos().z);
        return 1;
    }
}
