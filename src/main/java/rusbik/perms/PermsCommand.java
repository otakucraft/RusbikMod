package rusbik.perms;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import rusbik.Rusbik;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandSource.suggestMatching;

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

    public static int givePerms(ServerCommandSource source, String player, int value) throws CommandSyntaxException {
        PermsFileManager.setPerm(source.getPlayer(), player, value);
        return 1;
    }
}
