package rusbik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import rusbik.Rusbik;
import rusbik.database.RusbikDatabase;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PermsCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("perms").
                requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).
                then(literal("give").
                        then(argument("player", word()).
                                suggests((c, b) -> suggestMatching(Rusbik.getPlayers(c.getSource()), b)).
                                then(argument("int", IntegerArgumentType.integer(1, 3))
                                        .executes(context -> givePerms(context.getSource(), StringArgumentType.getString(context, "player"), IntegerArgumentType.getInteger(context, "int")))))));
    }

    public static int givePerms(ServerCommandSource source, String player, int value) {
        try {
            RusbikDatabase.updatePerms(player, value);
            source.sendFeedback(new LiteralText(String.format("Player %s => %d", player, value)), false);
        }
        catch (Exception ignored){}
        return 1;
    }
}
