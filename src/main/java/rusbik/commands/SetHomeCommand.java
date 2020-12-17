package rusbik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import rusbik.Rusbik;
import rusbik.database.RusbikDatabase;

import static net.minecraft.server.command.CommandManager.literal;

public class SetHomeCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("setHome").
                executes(context -> setHome(context.getSource())));
    }

    public static int setHome(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity playerEntity = source.getPlayer();
        if (playerEntity != null){
            try {
                if (RusbikDatabase.getPlayerPerms(source.getPlayer().getName().getString()) > 0){
                    RusbikDatabase.addPlayerInformation(playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), Rusbik.getDim(playerEntity.world));
                    source.sendFeedback(new LiteralText("Casa en: " + Rusbik.getDimensionWithColor(playerEntity.world) + Rusbik.formatCoords(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ())), false);
                }
                else {
                    source.sendFeedback(new LiteralText("No puedes usar este comando :P"), false);
                }
            }
            catch (Exception e){
                System.out.println(e);
            }
        }
        return 1;
    }
}
