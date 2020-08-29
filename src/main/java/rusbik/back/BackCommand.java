package rusbik.back;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import rusbik.Rusbik;
import rusbik.database.RusbikDatabase;

import static net.minecraft.server.command.CommandManager.literal;

public class BackCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("back").
                executes(context -> tpDeathPos(context.getSource())));
    }
    private static int tpDeathPos(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity playerEntity = source.getPlayer();
        if (playerEntity != null){
            try {
                if (RusbikDatabase.getPlayerPerms(source.getPlayer().getName().getString()) > 2){
                    BackManager.tpDeathPos(source.getPlayer());
                }
                else source.sendFeedback(new LiteralText("No puedes usar este comando :P"), false);
            }
            catch (Exception e){
                System.out.println(e);
            }
        }
        return 1;
    }
}
