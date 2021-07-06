package com.kahzerx.rubik;

import com.kahzerx.rubik.commands.*;
import com.kahzerx.rubik.database.RusbikBlockActionPerformLog;
import com.kahzerx.rubik.database.RusbikDatabase;
import com.kahzerx.rubik.discord.utils.DiscordListener;
import com.kahzerx.rubik.helpers.RusbikPlayer;
import com.kahzerx.rubik.settings.RubiConfig;
import com.kahzerx.rubik.utils.FileManager;
import com.kahzerx.rubik.utils.KrusbibUtils;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.storage.LevelStorage;

import java.sql.SQLException;
import java.util.HashMap;

public final class Rusbik {
    public static RubiConfig config;
    private static MinecraftServer minecraftServer;
    public static HashMap<String, RusbikPlayer> players = new HashMap<>();

    private Rusbik() { }

    public static void registerCommands(final CommandDispatcher<ServerCommandSource> dispatcher) {
        // AfkCommand.register(dispatcher);
        RandomTpCommand.register(dispatcher);
        SetHomeCommand.register(dispatcher);
        HomeCommand.register(dispatcher);
        PermsCommand.register(dispatcher);
        CustomTeleportCommand.register(dispatcher);
        AdminTeleportCommand.register(dispatcher);
        BackCommand.register(dispatcher);
        DiscordCommand.register(dispatcher);
        CameraCommand.register(dispatcher);
        SurvivalCommand.register(dispatcher);
        HereCommand.register(dispatcher);
        SpoofCommand.register(dispatcher);
        PitoCommand.register(dispatcher);
        BlockInfoCommand.register(dispatcher);
    }

    public static void onRunServer(final MinecraftServer minecraftServer, final LevelStorage.Session session) {
        Rusbik.minecraftServer = minecraftServer;

        RusbikDatabase.initializeDB(session.getDirectoryName());  // Crear si fuera necesario y establecer con conexión con la base de datos.

        if (!session.getDirectoryName().equals("")) {
            FileManager.jsonConfigFile = String.format("%s/%s", session.getDirectoryName(), FileManager.jsonConfigFile);
        }

        try {
            FileManager.initializeJson();  // Cargar la configuración del archivo .yaml
            if (Rusbik.config.getChatChannelId() != 0 && !Rusbik.config.getDiscordToken().equals("")) {
                if (Rusbik.config.isRunning()) {  // Iniciar el bot de discord.
                    try {
                        DiscordListener.connect(minecraftServer, Rusbik.config.getDiscordToken(), String.valueOf(Rusbik.config.getChatChannelId()));
                        DiscordListener.sendMessage("\\o/");  // Avisar que el server está abierto xd.
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("config file not created");
        }
    }

    public static void onStopServer() throws InterruptedException, SQLException {
        if (RusbikDatabase.logger.isAlive()) {
            RusbikDatabase.logger.running = false; // Needs database connection until it stops.
            RusbikDatabase.logger.join();
        }
        if (RusbikDatabase.c != null) {
            RusbikDatabase.c.close();
        }
        if (DiscordListener.chatBridge) {
            DiscordListener.stop();
        }
    }

    public static void onAutoSave() throws SQLException {
        if (!RusbikDatabase.logger.isAlive()) {
            try {
                RusbikDatabase.logger.clear();
                RusbikDatabase.logger.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        DiscordListener.checkSub(RusbikDatabase.getIDs());

        new Thread(() -> {
            try {
                RusbikDatabase.clearLogger();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }).start();
    }

    public static void onPlayerJoins(final ServerPlayerEntity player) throws SQLException {
        if (DiscordListener.chatBridge) {
            DiscordListener.sendMessage(":arrow_right: **" + player.getName().getString().replace("_", "\\_") + " joined the game!**");
        }

        if (!RusbikDatabase.hasRow(player.getName().getString())) {
            return;
        }

        RusbikDatabase.addPlayer(player.getName().getString());

        if (RusbikDatabase.playerFirstJoined(player.getName().getString())) {
            RusbikDatabase.updateCount(player.getName().getString());
            for (Team team : minecraftServer.getScoreboard().getTeams()) {
                if (team.getName().equals("MIEMBRO")) {
                    minecraftServer.getScoreboard().addPlayerToTeam(player.getName().getString(), team);
                }
            }
        }
    }

    public static void onPlayerLeaves(final ServerPlayerEntity playerEntity) {
        if (DiscordListener.chatBridge) {
            DiscordListener.sendMessage(":arrow_left: **" + playerEntity.getName().getString().replace("_", "\\_") + " left the game!**");
        }
        
        RusbikDatabase.removePlayer(playerEntity.getName().getString());
    }

    public static void onPlayerDies(final ServerPlayerEntity playerEntity) throws SQLException {
        if (DiscordListener.chatBridge) {
            DiscordListener.sendMessage(":skull_crossbones: **" + playerEntity.getDamageTracker().getDeathMessage().getString().replace("_", "\\_") + "**");
        }

        playerEntity.sendMessage(new LiteralText(String.format("RIP ;( %s %s", KrusbibUtils.getDimensionWithColor(playerEntity.world), KrusbibUtils.formatCoords(playerEntity.getPos().x, playerEntity.getPos().y, playerEntity.getPos().z))), false);

        if (RusbikDatabase.userExists(playerEntity.getName().getString())) {
            RusbikDatabase.updateDeathInformation(playerEntity.getName().getString(), playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), KrusbibUtils.getDim(playerEntity.world));
        }
    }

    public static void onChatMessage(final ServerPlayerEntity player, final String chatMessage) {
        if (!chatMessage.startsWith("/")) {
            DiscordListener.sendMessage("`<" + player.getName().getString() + ">` " + chatMessage);
        } else {
            System.out.printf("<%s> %s%n", player.getName().getString(), chatMessage);
        }
    }

    public static void onAdvancement(final String advancement) {
        if (DiscordListener.chatBridge) {
            DiscordListener.sendMessage(":confetti_ball: **" + advancement + "**");
        }
    }

    public static void onBlockInteraction(final String player,
                                          final String block,
                                          final BlockPos pos,
                                          final String dim,
                                          final int actionType) {
        RusbikDatabase.logger.log(new RusbikBlockActionPerformLog(
                player,
                block,
                pos.getX(), pos.getY(), pos.getZ(),
                dim,
                actionType,
                KrusbibUtils.getDate())
        );
    }
}
