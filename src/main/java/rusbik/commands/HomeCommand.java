package rusbik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import rusbik.database.RusbikDatabase;
import rusbik.helpers.HomeManager;

import static net.minecraft.server.command.CommandManager.literal;

public class HomeCommand {
    // Tp a "home".
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("home").
                executes(context -> tpHome(context.getSource())));
    }

    private static int tpHome(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity playerEntity = source.getPlayer();
        if (playerEntity != null){
            try{
                if (RusbikDatabase.userExists(playerEntity.getName().getString())) {
                    // Intenta hacer tp a la dirección guardada en la base de datos.
                    if (RusbikDatabase.getPlayerPerms(source.getPlayer().getName().getString()) > 0) HomeManager.tpHome(source.getPlayer());
                    else source.sendFeedback(new LiteralText("No puedes usar este comando :P"), false);
                }
                else source.sendFeedback(new LiteralText("Parece que no estás registrado correctamente y no puedes ejecutar esta acción."), false);
            }
            catch (Exception e){
                System.out.println(e);
            }
        }
        return 1;
    }
}
