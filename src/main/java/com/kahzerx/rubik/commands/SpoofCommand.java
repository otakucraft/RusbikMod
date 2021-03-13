package com.kahzerx.rubik.commands;

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
import com.kahzerx.rubik.utils.KrusbibUtils;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.command.CommandSource.suggestMatching;

public final class SpoofCommand {
    /**
     * Stalkear inventarios privados de jugadores conectados.
     * @param dispatcher register command.
     */
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("spoof").
                requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).
                then(CommandManager.argument("player", StringArgumentType.word()).
                        suggests((c, b) -> suggestMatching(KrusbibUtils.getPlayers(c.getSource()), b)).
                        then(CommandManager.literal("enderChest").
                                executes(context -> spoofEC(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "player")))).
                        then(CommandManager.literal("inventory").
                                executes(context -> spoofInv(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "player"))))));
    }

    private SpoofCommand() { }

    public static int spoofEC(final ServerCommandSource source, final String playerE) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        ServerPlayerEntity player2 = source.getMinecraftServer().getPlayerManager().getPlayer(playerE);
        if (player2 != null) {
            EnderChestInventory enderChestInventory = player2.getEnderChestInventory();
            // Generar la pantalla de enderChest.
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) ->
                    GenericContainerScreenHandler.createGeneric9x3(i, playerInventory, enderChestInventory),
                    new LiteralText(String.format("%s stop hax >:(", player.getName().getString()))));
        } else {
            source.sendFeedback(new LiteralText("player offline"), false);
        }
        return 1;
    }

    public static int spoofInv(final ServerCommandSource source, final String playerE) throws CommandSyntaxException {
        final int invSize = 54;
        final int hotBarSize = 9;
        final int hotBarStartPos = 27;
        final int invStartPos = 9;
        Inventory inventory = new SimpleInventory(invSize);
        ServerPlayerEntity player = source.getPlayer();
        ServerPlayerEntity player2 = source.getMinecraftServer().getPlayerManager().getPlayer(playerE);
        assert player2 != null;

        for (int i = 0; i < player2.inventory.main.size(); i++) {
            if (i < hotBarSize) {
                inventory.setStack(i + hotBarStartPos, player2.inventory.main.get(i));
            } else {
                inventory.setStack(i - invStartPos, player2.inventory.main.get(i));
            }
        }

        final int armorSlotStartPos = 45;
        for (int j = 0; j < player2.inventory.armor.size(); j++) {
            inventory.setStack(j + armorSlotStartPos, player2.inventory.armor.get(j));
        }

        final int offHandSlotPos = 36;
        inventory.setStack(offHandSlotPos, player2.inventory.offHand.get(0));

        player.openHandledScreen(
                new SimpleNamedScreenHandlerFactory(
                        (i, playerInventory, playerEntity) ->
                                GenericContainerScreenHandler.createGeneric9x6(i, playerInventory, inventory),
                        new LiteralText(
                                String.format(
                                        "%s stop hax >:(",
                                        player.getName().getString()))));
        return 1;
    }
}
