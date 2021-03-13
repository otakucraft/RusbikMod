package com.kahzerx.rubik.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import static net.minecraft.server.command.CommandManager.literal;

public final class PitoCommand {
    /**
     * ...
     * @param dispatcher register command.
     */
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("pito").
                executes(context -> msg(context.getSource())));
    }

    private PitoCommand() { }

    public static int msg(final ServerCommandSource source) {
        source.sendFeedback(new LiteralText("Buena tula mi rey."), false);
        return 1;
    }
}
