package rusbik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import rusbik.Rusbik;
import rusbik.discord.utils.DiscordListener;

import static net.minecraft.server.command.CommandManager.literal;

public class DiscordCommand {
    // Comando para vincular discord con minecraft, para hacer gestión de whitelist, chat bridge, etc.
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("discord").
                requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).
                then(CommandManager.literal("setBot").
                        then(CommandManager.argument("token", StringArgumentType.string()).
                                then(CommandManager.argument("channelId", StringArgumentType.string()).
                                        executes(context -> setup(context.getSource(),
                                                StringArgumentType.getString(context, "token"),
                                                StringArgumentType.getString(context, "channelId")))))).
                then(CommandManager.literal("stop").
                        executes(context -> stop(context.getSource()))).
                then(CommandManager.literal("start").
                        executes(context -> start(context.getSource()))).
                then(CommandManager.literal("discordRoleID").
                        then(CommandManager.argument("ID", LongArgumentType.longArg()).
                                executes(context -> setDiscordRole(context.getSource(), LongArgumentType.getLong(context, "ID")))).
                        executes(context -> getDiscordRole(context.getSource()))).
                then(CommandManager.literal("groupID").
                        then(CommandManager.argument("ID", LongArgumentType.longArg()).
                                executes(context -> setGroupID(context.getSource(), LongArgumentType.getLong(context, "ID")))).
                        executes(context -> getGroupID(context.getSource()))).
                then(CommandManager.literal("adminChat").
                        then(CommandManager.literal("add").
                                then(CommandManager.argument("ID", LongArgumentType.longArg()).
                                        executes(context -> addID(context.getSource(), LongArgumentType.getLong(context, "ID"), 0)))).
                        then(CommandManager.literal("list").
                                executes(context -> listID(context.getSource(), 0))).
                        then(CommandManager.literal("remove").
                                then(CommandManager.argument("ID", LongArgumentType.longArg()).
                                        executes(context -> removeID(context.getSource(), LongArgumentType.getLong(context, "ID"), 0))))).
                then(CommandManager.literal("whitelistChat").
                        then(CommandManager.literal("add").
                                then(CommandManager.argument("ID", LongArgumentType.longArg()).
                                        executes(context -> addID(context.getSource(), LongArgumentType.getLong(context, "ID"), 1)))).
                        then(CommandManager.literal("list").
                                executes(context -> listID(context.getSource(), 1))).
                        then(CommandManager.literal("remove").
                                then(CommandManager.argument("ID", LongArgumentType.longArg()).
                                        executes(context -> removeID(context.getSource(), LongArgumentType.getLong(context, "ID"), 1))))).
                then(CommandManager.literal("allowedChat").
                        then(CommandManager.literal("add").
                                then(CommandManager.argument("ID", LongArgumentType.longArg()).
                                        executes(context -> addID(context.getSource(), LongArgumentType.getLong(context, "ID"), 2)))).
                        then(CommandManager.literal("list").
                                executes(context -> listID(context.getSource(), 2))).
                        then(CommandManager.literal("remove").
                                then(CommandManager.argument("ID", LongArgumentType.longArg()).
                                        executes(context -> removeID(context.getSource(), LongArgumentType.getLong(context, "ID"), 2))))).
                then(CommandManager.literal("validRoles").
                        then(CommandManager.literal("add").
                                then(CommandManager.argument("ID", LongArgumentType.longArg()).
                                        executes(context -> addID(context.getSource(), LongArgumentType.getLong(context, "ID"), 3)))).
                        then(CommandManager.literal("list").
                                executes(context -> listID(context.getSource(), 3))).
                        then(CommandManager.literal("remove").
                                then(CommandManager.argument("ID", LongArgumentType.longArg()).
                                        executes(context -> removeID(context.getSource(), LongArgumentType.getLong(context, "ID"), 3))))).
                executes(context -> info(context.getSource())));
    }

    private static int addID(ServerCommandSource source, long id, int type) {
        boolean action = false;
        switch (type) {
            case 0:
                if (!Rusbik.config.getAdminChat().contains(id)) {
                    Rusbik.config.addAdminChat(id);
                    action = true;
                }
                break;
            case 1:
                if (!Rusbik.config.getWhitelistChat().contains(id)) {
                    Rusbik.config.addWhitelistChat(id);
                    action = true;
                }
                break;
            case 2:
                if (!Rusbik.config.getAllowedChat().contains(id)) {
                    Rusbik.config.addAllowedChat(id);
                    action = true;
                }
                break;
            case 3:
                if (!Rusbik.config.getValidRoles().contains(id)) {
                    Rusbik.config.addValidRoles(id);
                    action = true;
                }
                break;
        }
        source.sendFeedback(new LiteralText(action ? "ID added." : "This ID already exists."), false);
        return 1;
    }

    private static int removeID(ServerCommandSource source, long id, int type) {
        boolean action = false;
        switch (type) {
            case 0:
                if (Rusbik.config.getAdminChat().contains(id)) {
                    Rusbik.config.removeAdminChat(id);
                    action = true;
                }
                break;
            case 1:
                if (Rusbik.config.getWhitelistChat().contains(id)) {
                    Rusbik.config.removeWhitelistChat(id);
                    action = true;
                }
                break;
            case 2:
                if (Rusbik.config.getAllowedChat().contains(id)) {
                    Rusbik.config.removeAllowedChat(id);
                    action = true;
                }
                break;
            case 3:
                if (Rusbik.config.getValidRoles().contains(id)) {
                    Rusbik.config.removeValidRoles(id);
                    action = true;
                }
                break;
        }
        source.sendFeedback(new LiteralText(action ? "ID removed." : "This ID doesn't exist."), false);
        return 1;
    }

    private static int listID(ServerCommandSource source, int type) {
        switch (type) {
            case 0:
                source.sendFeedback(new LiteralText(Rusbik.config.getAdminChat().toString()), false);
                break;
            case 1:
                source.sendFeedback(new LiteralText(Rusbik.config.getWhitelistChat().toString()), false);
                break;
            case 2:
                source.sendFeedback(new LiteralText(Rusbik.config.getAllowedChat().toString()), false);
                break;
            case 3:
                source.sendFeedback(new LiteralText(Rusbik.config.getValidRoles().toString()), false);
                break;
        }
        return 1;
    }

    private static int setGroupID(ServerCommandSource source, long id) {
        Rusbik.config.setGroupID(id);
        getDiscordRole(source);
        return 1;
    }

    private static int getGroupID(ServerCommandSource src) {
        src.sendFeedback(new LiteralText("The actual groupID is: " + Rusbik.config.getGroupID()), false);
        return 1;
    }

    private static int setDiscordRole(ServerCommandSource source, long id) {
        Rusbik.config.setDiscordRole(id);
        getDiscordRole(source);
        return 1;
    }

    private static int getDiscordRole(ServerCommandSource src) {
        src.sendFeedback(new LiteralText("The actual role is: " + Rusbik.config.getDiscordRole()), false);
        return 1;
    }

    // Configurar la token para el bot y el channelId para el chat bridge.
    private static int setup(ServerCommandSource src, String token, String channelId){
        if (DiscordListener.chatBridge){
            src.sendFeedback(new LiteralText("Please stop the server before you make any changes"), false);
        }
        else{
            Rusbik.config.setDiscordToken(token);
            Rusbik.config.setChatChannelId(Long.parseLong(channelId));
            src.sendFeedback(new LiteralText("Done!"), false);
        }
        return 1;
    }

    // Detener el bot de discord.
    private static int stop(ServerCommandSource src){
        if (DiscordListener.chatBridge){
            DiscordListener.stop();
            Rusbik.config.setRunning(false);
            src.sendFeedback(new LiteralText("Discord integration has stopped"), false);
        }
        else{
            src.sendFeedback(new LiteralText("Discord integration is already off"), false);
        }
        return 1;
    }

    // Inicializar el bot.
    private static int start(ServerCommandSource src){
        if (!DiscordListener.chatBridge){
            if (Rusbik.config.getChatChannelId() != 0 && !Rusbik.config.getDiscordToken().equals("")) {
                try {
                    // Envío la información al bot para que inicie.
                    DiscordListener.connect(src.getMinecraftServer(), Rusbik.config.getDiscordToken(), String.valueOf(Rusbik.config.getChatChannelId()));
                    src.sendFeedback(new LiteralText("Discord integration is running"), false);
                } catch (Exception e) {
                    e.printStackTrace();
                    src.sendFeedback(new LiteralText("Unable to start the process, is the token correct?"), false);
                }
            }
            else{
                src.sendFeedback(new LiteralText("Set up a bot first please"), false);
            }
        }
        else{
            src.sendFeedback(new LiteralText("Discord integration is already on"), false);
        }
        return 1;
    }

    // Información del estado del bot.
    private static int info(ServerCommandSource src){
        if (DiscordListener.chatBridge) src.sendFeedback(new LiteralText("Chat bridge is currently on!"), false);
        else src.sendFeedback(new LiteralText("Chat bridge is currently off!"), false);

        return 1;
    }
}
