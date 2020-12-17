package rusbik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.world.GameMode;
import rusbik.Rusbik;
import rusbik.database.RusbikDatabase;

import static net.minecraft.server.command.CommandManager.literal;

public class RusbisCameraCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("c").
                executes(context -> setCameraMode(context.getSource())));
    }

    public static int setCameraMode(ServerCommandSource source) {
        try {
            if (RusbikDatabase.getPlayerPerms(source.getPlayer().getName().getString()) > 2) {
                source.getPlayer().setGameMode(GameMode.SPECTATOR);
                source.getPlayer().addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 999999, 0, false, false));
                source.getPlayer().addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, 999999, 0, false, false));
            }
            else source.sendFeedback(new LiteralText("No puedes usar este comando :P"), false);
        }
        catch (Exception e){
            source.sendFeedback(new LiteralText("No ha sido posible ejecutar este comando"), false);
        }

        return 1;
    }
}
