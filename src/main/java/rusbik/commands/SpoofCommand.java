package rusbik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import rusbik.utils.KrusbibUtils;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.command.CommandSource.suggestMatching;

public class SpoofCommand {
    // Stalkear inventarios privados de jugadores conectados.
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("spoof").
                requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).
                then(CommandManager.argument("player", StringArgumentType.word()).
                        suggests((c, b) -> suggestMatching(KrusbibUtils.getPlayers(c.getSource()), b)).
                        then(CommandManager.literal("enderChest").  // Solo disponible el enderChest por ahora.
                                executes(context -> spoofEC(context.getSource(), StringArgumentType.getString(context, "player"))))));
    }

    public static int spoofEC(ServerCommandSource source, String playerE) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        ServerPlayerEntity player2 = source.getMinecraftServer().getPlayerManager().getPlayer(playerE);
        if (player2 != null){
            EnderChestInventory enderChestInventory = player2.getEnderChestInventory();
            // Generar la pantalla de enderChest.
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                    GenericContainerScreenHandler.createGeneric9x3(i, playerInventory, enderChestInventory),
                    new LiteralText(String.format("%s stop hax >:(", player.getName().getString()))));
        }
        else {
            source.sendFeedback(new LiteralText("player offline"), false);
        }
        return 1;
    }
}
