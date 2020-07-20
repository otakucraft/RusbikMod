package rusbik.home;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import rusbik.Rusbik;

import static net.minecraft.server.command.CommandManager.literal;

public class SetHomeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("setHome").
                executes(context -> setHome(context.getSource())));
    }

    public static int setHome(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity playerEntity = source.getPlayer();
        if (playerEntity instanceof ServerPlayerEntity){
            if (Integer.parseInt(Rusbik.permsArray.get(source.getPlayer().getName().getString())) > 0){
                HomeFileManager.setHome(source.getPlayer(),source.getWorld(), source.getPlayer().getPos().x, source.getPlayer().getPos().y, source.getPlayer().getPos().z);
            }
            else source.sendFeedback(new LiteralText("No puedes usar este comando :P"), false);
        }
        return 1;
    }
}
