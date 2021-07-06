package com.kahzerx.rubik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.GameMode;
import com.kahzerx.rubik.database.RusbikDatabase;
import com.kahzerx.rubik.utils.KrusbibUtils;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.command.CommandSource.suggestMatching;

public final class CustomTeleportCommand {
    /**
     * Comando creado para que los moderadores puedan
     * hacerse tp a otros jugadores,
     * solo tiene 1 uso y reemplaza al "tp" vanilla.
     * @param dispatcher register command.
     */
    public static void register(
            final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("tp").
                then(CommandManager.argument(
                        "player",
                        StringArgumentType.word()).
                        suggests((c, b) -> suggestMatching(
                                KrusbibUtils.getPlayers(c.getSource()),
                                b)).
                        executes(context -> tp(
                                context.getSource(),
                                StringArgumentType.getString(
                                        context,
                                        "player")))));
    }

    private CustomTeleportCommand() { }

    // No es más que la función de adminTp de tp de administrador a jugador.
    private static int tp(
            final ServerCommandSource source,
            final String player) {
        ServerPlayerEntity playerEntity = source.
                getServer().
                getPlayerManager().
                getPlayer(player);
        final int duration = 999999;
        if (playerEntity != null) {
            try {
                if (RusbikDatabase.userExists(playerEntity.
                        getName().
                        getString())) {
                    if (RusbikDatabase.getPlayerPerms(source.
                            getPlayer().
                            getName().
                            getString()) > 1) {
                        if (playerEntity.isSpectator()) {
                            source.getPlayer().changeGameMode(GameMode.SPECTATOR);
                            source.getPlayer().addStatusEffect(
                                    new StatusEffectInstance(
                                            StatusEffects.NIGHT_VISION,
                                            duration,
                                            0,
                                            false,
                                            false));
                            source.getPlayer().addStatusEffect(
                                    new StatusEffectInstance(
                                            StatusEffects.CONDUIT_POWER,
                                            duration,
                                            0,
                                            false,
                                            false));
                            source.getPlayer().teleport(
                                    playerEntity.getServerWorld(),
                                    playerEntity.getPos().x,
                                    playerEntity.getPos().y,
                                    playerEntity.getPos().z,
                                    source.getPlayer().getYaw(),
                                    source.getPlayer().getPitch());
                            source.sendFeedback(
                                    new LiteralText(
                                            "Recuerda usar /s "
                                                    + "para volver a survival"),
                                    false);
                        } else {
                            source.getPlayer().teleport(
                                    playerEntity.getServerWorld(),
                                    playerEntity.getPos().x,
                                    playerEntity.getPos().y,
                                    playerEntity.getPos().z,
                                    source.getPlayer().getYaw(),
                                    source.getPlayer().getPitch());
                        }
                    } else {
                        source.sendFeedback(
                                new LiteralText(
                                        "No puedes usar este comando :P"),
                                false);
                    }
                } else {
                    source.sendFeedback(
                            new LiteralText(
                                    "Parece que no estas registrado "
                                            + "correctamente y no puedes "
                                            + "ejecutar esta acción."),
                            false);
                }
            } catch (Exception e) {
                source.sendFeedback(
                        new LiteralText(
                                "No ha sido posible ejecutar este comando"),
                        false);
            }
        } else {
            source.sendFeedback(
                    new LiteralText(
                            "Este jugador no existe D:"),
                    false);
        }
        return 1;
    }
}
