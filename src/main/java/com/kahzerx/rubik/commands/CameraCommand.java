package com.kahzerx.rubik.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.world.GameMode;
import com.kahzerx.rubik.database.RusbikDatabase;

import static net.minecraft.server.command.CommandManager.literal;

public final class CameraCommand {
    /**
     * El /c robado de carpet.
     * @param dispatcher register command.
     */
    public static void register(
            final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("c").
                executes(context -> setCameraMode(context.getSource())));
    }

    private CameraCommand() { }

    /**
     * Set Camera mode for player.
     * @param source to get playerEntity.
     * @return Command success.
     */
    public static int setCameraMode(
            final ServerCommandSource source) {
        try {
            if (RusbikDatabase.userExists(source.
                    getPlayer().
                    getName().
                    getString())) {
                // Privilegios de nivel 2.
                if (RusbikDatabase.getPlayerPerms(
                        source.getPlayer().getName().getString()) > 2) {
                    final int duration = 999999;
                    source.getPlayer().changeGameMode(GameMode.SPECTATOR);
                    source.getPlayer().addStatusEffect(
                            new StatusEffectInstance(
                                    StatusEffects.NIGHT_VISION,
                                    duration,
                                    0,
                                    false,
                                    false));
                    source.getPlayer().addStatusEffect(
                            new StatusEffectInstance(
                                    StatusEffects.CONDUIT_POWER,
                                    duration,
                                    0,
                                    false,
                                    false));
                } else {
                    source.sendFeedback(
                            new LiteralText(
                                    "No puedes usar este comando :P"),
                            false);
                }
            } else {
                source.sendFeedback(
                        new LiteralText(
                                "Parece que no estás registrado correctamente "
                                        + "y no puedes ejecutar esta acción."),
                        false);
            }
        } catch (Exception e) {
            source.sendFeedback(
                    new LiteralText(
                    "No ha sido posible ejecutar este comando"),
                    false);
        }
        return 1;
    }
}
