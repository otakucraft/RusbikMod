package com.kahzerx.rubik.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class AfkCommand {
    /**
     * Comando afk que no hace absolutamente nada.
     * @param dispatcher usado para registrar el comando.
     */
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("afk").
                executes(context -> afk()));
    }

    private static int afk() {
        // Literalmente nada.
        return 1;
    }
}
