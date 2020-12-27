package rusbik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
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
                        then(CommandManager.literal("enderChest").
                                executes(context -> spoofEC(context.getSource(), StringArgumentType.getString(context, "player")))).
                        then(CommandManager.literal("inventory").
                                executes(context -> spoofInv(context.getSource(), StringArgumentType.getString(context, "player"))))));
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

    public static int spoofInv(ServerCommandSource source, String playerE) throws CommandSyntaxException {
        Inventory inventory = new SimpleInventory(54);
        ServerPlayerEntity player = source.getPlayer();
        ServerPlayerEntity player2 = source.getMinecraftServer().getPlayerManager().getPlayer(playerE);
        assert player2 != null;

        for (int i = 0; i < player2.inventory.main.size(); i++) {
            if (i < 9) {
                inventory.setStack(i + 27, player2.inventory.main.get(i));
            }
            else {
                inventory.setStack(i - 9, player2.inventory.main.get(i));
            }
        }

        for (int j = 0; j < player2.inventory.armor.size(); j++) {
            inventory.setStack(j + 45, player2.inventory.armor.get(j));
        }

        inventory.setStack(36, player2.inventory.offHand.get(0));

        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inventory), new LiteralText(String.format("%s stop hax >:(", player.getName().getString()))));
        return 1;
    }
}
