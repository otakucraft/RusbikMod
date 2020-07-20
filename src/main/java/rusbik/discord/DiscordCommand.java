package rusbik.discord;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import static net.minecraft.server.command.CommandManager.literal;

public class DiscordCommand {
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

    private static int setup(ServerCommandSource src, String token, String channelId){
        if (DiscordListener.chatBridge){
            src.sendFeedback(new LiteralText("Please stop the CollaxGaming before you make any changes"), false);
        }
        else{
            DiscordFileManager.writeFile(token, channelId, false);
            src.sendFeedback(new LiteralText("Done!"), false);
        }
        return 1;
    }

    private static int stop(ServerCommandSource src){
        if (DiscordListener.chatBridge){
            DiscordListener.stop();
            DiscordFileManager.updateFile(false);
            src.sendFeedback(new LiteralText("Discord integration has stopped"), false);
        }
        else{
            src.sendFeedback(new LiteralText("Discord integration is already off"), false);
        }
        return 1;
    }

    private static int start(ServerCommandSource src){
        String[] result = DiscordFileManager.readFile();
        if (!DiscordListener.chatBridge){
            if (!result[0].equals("") && !result[1].equals("") && !result[2].equals("")) {
                try {
                    DiscordListener.connect(src.getMinecraftServer(), result[0], result[1]);
                    src.sendFeedback(new LiteralText("Discord integration is running"), false);
                } catch (Exception e) {
                    System.out.println(e);
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

    private static int info(ServerCommandSource src){
        if (DiscordListener.chatBridge) src.sendFeedback(new LiteralText("Chat bridge is currently on!"), false);
        else src.sendFeedback(new LiteralText("Chat bridge is currently off!"), false);

        return 1;
    }
}
