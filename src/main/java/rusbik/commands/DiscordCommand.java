package rusbik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import rusbik.Rusbik;
import rusbik.discord.utils.DiscordListener;

import static net.minecraft.server.command.CommandManager.literal;

public class DiscordCommand {
    // Comando para vincular discord con minecraft, para hacer gestión de whitelist, chatbridge, etc.
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("discord")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(CommandManager.literal("setBot")
                        .then(CommandManager.argument("token", StringArgumentType.string())
                                .then(CommandManager.argument("channelId", StringArgumentType.string())
                                        .executes(context -> setup(context.getSource(), StringArgumentType.getString(context, "token"), StringArgumentType.getString(context, "channelId"))))))
                .then(CommandManager.literal("stop")
                        .executes(context -> stop(context.getSource())))
                .then(CommandManager.literal("start")
                        .executes(context -> start(context.getSource())))
                .executes(context -> info(context.getSource())));
    }

    // Configurar la token para el bot y el channelId para el chatbridge.
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
            if (Rusbik.config.chatChannelId != 0 && !Rusbik.config.discordToken.equals("")) {
                try {
                    // Envio la información al bot para que inicie.
                    DiscordListener.connect(src.getMinecraftServer(), Rusbik.config.discordToken, String.valueOf(Rusbik.config.chatChannelId));
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
