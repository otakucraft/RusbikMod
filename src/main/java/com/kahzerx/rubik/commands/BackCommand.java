package com.kahzerx.rubik.commands;

import com.kahzerx.rubik.helpers.BackManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import com.kahzerx.rubik.database.RusbikDatabase;

import java.sql.SQLException;

import static net.minecraft.server.command.CommandManager.literal;

public final class BackCommand {
    /**
     * Comando back, para hacerte tp a tu última posición de muerte.
     * @param dispatcher usado para registrar el comando
     */
    public static void register(
            final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("back").
                executes(context -> tpDeathPos(context.getSource())));
    }

    private BackCommand() { }

    private static int tpDeathPos(
            final ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity playerEntity = source.getPlayer();
        if (playerEntity != null) {
            try {
                if (RusbikDatabase.userExists(
                        playerEntity.getName().getString())) {
                    if (RusbikDatabase.getPlayerPerms(
                            source.getPlayer().getName().getString()) > 2) {
                        BackManager.tpDeathPos(playerEntity);
                    } else {
                        source.sendFeedback(new LiteralText(
                                "No puedes usar este comando :P"
                        ), false);
                    }
                } else {
                    source.sendFeedback(new LiteralText(
                            "Parece que no estas registrado correctamente "
                                    + "y no puedes ejecutar esta acción."
                    ), false);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 1;
    }
}
