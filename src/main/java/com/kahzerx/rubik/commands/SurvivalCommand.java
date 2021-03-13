package com.kahzerx.rubik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.GameMode;

import static net.minecraft.server.command.CommandManager.literal;

public final class SurvivalCommand {
    /**
     * El /s robado de carpet.
     * @param dispatcher register command.
     */
    public static void register(
            final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("s").
                executes(context -> setSurvivalMode(context.getSource())));
    }

    private SurvivalCommand() { }

    public static int setSurvivalMode(final ServerCommandSource source)
            throws CommandSyntaxException {
        source.getPlayer().setGameMode(GameMode.SURVIVAL);
        source.getPlayer().removeStatusEffect(StatusEffects.CONDUIT_POWER);
        source.getPlayer().removeStatusEffect(StatusEffects.NIGHT_VISION);
        return 1;
    }
}
