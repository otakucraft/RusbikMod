package rusbik;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.level.storage.LevelStorage;
import rusbik.commands.*;
import rusbik.database.RusbikBlockActionPerformLog;
import rusbik.database.RusbikDatabase;
import rusbik.discord.utils.DiscordListener;
import rusbik.helpers.RusbikPlayer;
import rusbik.settings.RubiConfig;
import rusbik.utils.FileManager;
import rusbik.utils.KrusbibUtils;

import java.sql.SQLException;
import java.util.HashMap;

public class Rusbik {

    public static RubiConfig config;
    private static MinecraftServer minecraftServer;
    public static final HashMap<String, RusbikPlayer> players = new HashMap<>();

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
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
        SBCommand.register(dispatcher);
    }

    public static void onRunServer(MinecraftServer minecraftServer, LevelStorage.Session session) {
        Rusbik.minecraftServer = minecraftServer;

        RusbikDatabase.initializeDB(session.getDirectoryName());  // Crear si fuera necesario y establecer con conexión con la base de datos.

        FileManager.directoryName = session.getDirectoryName();
        try {
            FileManager.initializeJson();  // Cargar la configuración del archivo .yaml
            if (Rusbik.config.getChatChannelId() != 0 && !Rusbik.config.getDiscordToken().equals("")) {
                if (Rusbik.config.isRunning()) {  // Iniciar el bot de discord.
                    try {
                        DiscordListener.connect(minecraftServer, Rusbik.config.getDiscordToken(), String.valueOf(Rusbik.config.getChatChannelId()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("config file not created");
        }
    }

    public static void onStopServer() throws InterruptedException, SQLException {
        if (RusbikDatabase.logger.isAlive()) {
            RusbikDatabase.logger.running = false; // Needs database connection until it stops
            RusbikDatabase.logger.join();
        }
        if (RusbikDatabase.c != null) RusbikDatabase.c.close();
        if (DiscordListener.chatBridge) DiscordListener.stop();
    }

    public static void onAutoSave() throws SQLException {
        DiscordListener.checkSub(RusbikDatabase.getIDs());
        RusbikDatabase.clearLogger();
    }

    public static void onPlayerJoins(ServerPlayerEntity player) throws SQLException {
        if (DiscordListener.chatBridge) DiscordListener.sendMessage(":arrow_right: **" + player.getName().getString().replace("_", "\\_") + " joined the game!**");
        
        RusbikDatabase.addPlayer(player);

        if (!RusbikDatabase.playerExists(player.getName().getString())){
            RusbikDatabase.updateCount(player.getName().getString());
            for (Team team : minecraftServer.getScoreboard().getTeams()){
                if (team.getName().equals("MIEMBRO")){
                    minecraftServer.getScoreboard().addPlayerToTeam(player.getName().getString(), team);
                }
            }
        }
    }

    public static void onPlayerLeaves(ServerPlayerEntity playerEntity) throws SQLException {
        if (DiscordListener.chatBridge) DiscordListener.sendMessage(":arrow_left: **" + playerEntity.getName().getString().replace("_", "\\_") + " left the game!**");
        
        RusbikDatabase.removePlayer(playerEntity);
    }

    public static void onPlayerDies(ServerPlayerEntity playerEntity) throws SQLException {
        if (DiscordListener.chatBridge){
            DiscordListener.sendMessage(":skull_crossbones: **" + playerEntity.getDamageTracker().getDeathMessage().getString().replace("_", "\\_") + "**");
        }

        playerEntity.sendMessage(new LiteralText(String.format("RIP ;( %s %s", KrusbibUtils.getDimensionWithColor(playerEntity.world), KrusbibUtils.formatCoords(playerEntity.getPos().x, playerEntity.getPos().y, playerEntity.getPos().z))), false);

        if (RusbikDatabase.userExists(playerEntity.getEntityName())) {
            RusbikDatabase.updateDeathInformation(playerEntity.getEntityName(), playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), KrusbibUtils.getDim(playerEntity.world));
        }
    }

    public static void onChatMessage(ServerPlayerEntity player, String chatMessage) {
        if (!chatMessage.startsWith("/")) DiscordListener.sendMessage("`<" + player.getName().getString() + ">` " + chatMessage);
        else System.out.printf("<%s> %s%n", player.getName().getString(), chatMessage);
    }

    public static void onAdvancement(String advancement) {
        if (DiscordListener.chatBridge) DiscordListener.sendMessage(":confetti_ball: **" + advancement + "**");
    }

    public static void onBlockInteraction(String player, String block, BlockPos pos, String dim, int actionType) {
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
