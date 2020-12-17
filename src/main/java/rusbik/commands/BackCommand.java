package rusbik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import rusbik.helpers.BackManager;
import rusbik.database.RusbikDatabase;

import java.sql.SQLException;

import static net.minecraft.server.command.CommandManager.literal;

public class BackCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("back").
                executes(context -> tpDeathPos(context.getSource())));
    }
    private static int tpDeathPos(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity playerEntity = source.getPlayer();
        if (playerEntity != null) {
            try {
                if (RusbikDatabase.userExists(playerEntity.getName().getString())) {
                    if (RusbikDatabase.getPlayerPerms(source.getPlayer().getName().getString()) > 2)
                        BackManager.tpDeathPos(playerEntity);
                    else source.sendFeedback(new LiteralText("No puedes usar este comando :P"), false);
                }
                else source.sendFeedback(new LiteralText("Parece que no estas registrado correctamente y no puedes ejecutar esta acción."), false);
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
        return 1;
    }
}
