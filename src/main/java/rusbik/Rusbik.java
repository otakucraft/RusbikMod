package rusbik;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import rusbik.randomTp.RandomTpCommand;

public class Rusbik {
    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher){
        RandomTpCommand.register(dispatcher);
    }
}
