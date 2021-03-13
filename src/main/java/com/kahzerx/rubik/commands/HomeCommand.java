package com.kahzerx.rubik.commands;

import com.kahzerx.rubik.helpers.HomeManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import com.kahzerx.rubik.database.RusbikDatabase;

import static net.minecraft.server.command.CommandManager.literal;

public final class HomeCommand {
    // Tp a "home".
    public static void register(
            final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("home").
                executes(context -> tpHome(context.getSource())));
    }

    private HomeCommand() { }

    private static int tpHome(
            final ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity playerEntity = source.getPlayer();
        if (playerEntity != null) {
            try {
                if (RusbikDatabase.userExists(playerEntity.
                        getName().
                        getString())) {
                    // Intenta hacer tp a la dirección guardada en la base de datos.
                    if (RusbikDatabase.getPlayerPerms(source.
                            getPlayer().
                            getName().
                            getString()) > 0) {
                        HomeManager.tpHome(source.getPlayer());
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
                e.printStackTrace();
            }
        }
        return 1;
    }
}
