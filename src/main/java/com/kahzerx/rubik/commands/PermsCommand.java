package com.kahzerx.rubik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import com.kahzerx.rubik.database.RusbikDatabase;
import com.kahzerx.rubik.utils.KrusbibUtils;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class PermsCommand {
    /**
     * Actualizar sistema de privilegios.
     * @param dispatcher register command.
     */
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("perms").
                requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).
                then(literal("give").
                        then(argument("player", word()).
                                suggests((c, b) -> suggestMatching(KrusbibUtils.getPlayers(c.getSource()), b)).
                                then(argument("int", IntegerArgumentType.integer(1, 3))
                                        .executes(context -> givePerms(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "player"),
                                                IntegerArgumentType.getInteger(context, "int")))))));
    }

    private PermsCommand() { }

    public static int givePerms(final ServerCommandSource source, final String player, final int value) {
        try {
            if (RusbikDatabase.userExists(player)) {
                RusbikDatabase.updatePerms(player, value);
                source.sendFeedback(new LiteralText(String.format("Player %s => %d", player, value)), false);
            } else {
                source.sendFeedback(new LiteralText("Parece que este usuario registrado correctamente y no puedes ejecutar esta acción."), false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }
}
