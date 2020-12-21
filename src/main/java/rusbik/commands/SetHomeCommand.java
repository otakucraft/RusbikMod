package rusbik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;
import rusbik.database.RusbikDatabase;
import rusbik.utils.KrusbibUtils;

import static net.minecraft.server.command.CommandManager.literal;

public class SetHomeCommand {
    // Configurar una "home" para hacerte tp mediante el comando /home.
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("setHome").
                executes(context -> setHome(context.getSource())));
    }

    public static int setHome(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity playerEntity = source.getPlayer();
        if (playerEntity != null){
            try {
                if (RusbikDatabase.userExists(playerEntity.getName().getString())) {
                    if (RusbikDatabase.getPlayerPerms(source.getPlayer().getName().getString()) > 0) {
                        // Actualizar la base de datos y mensaje.
                        RusbikDatabase.updatePlayerInformation(playerEntity, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), KrusbibUtils.getDim(playerEntity.world));
                        playerEntity.setSpawnPoint(World.OVERWORLD, playerEntity.getBlockPos(), 0.0F, true, false);
                        source.sendFeedback(new LiteralText("Casa en: " + KrusbibUtils.getDimensionWithColor(playerEntity.world) + KrusbibUtils.formatCoords(playerEntity.getX(), playerEntity.getY(), playerEntity.getZ())), false);
                    }
                    else {
                        source.sendFeedback(new LiteralText("No puedes usar este comando :P"), false);
                    }
                }
                else source.sendFeedback(new LiteralText("Parece que no estás registrado correctamente y no puedes ejecutar esta acción."), false);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return 1;
    }
}
