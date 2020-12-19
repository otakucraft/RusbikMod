package rusbik.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.server.*;
import rusbik.Rusbik;
import rusbik.helpers.DiscordCommands;

import javax.annotation.Nonnull;

public class DiscordListener extends ListenerAdapter {
    private static JDA jda = null;
    public static String channelId = "";
    public static String token = "";
    public static boolean chatBridge = false;

    MinecraftServer server;

    public DiscordListener (MinecraftServer s){
        this.server = s;
    }

    public static void connect(MinecraftServer server, String t, String c){
        token = t;
        channelId = c;
        try{
            chatBridge = false;
            Rusbik.config.setRunning(false);
            jda = JDABuilder.createDefault(token).addEventListeners(new DiscordListener(server)).build();
            jda.awaitReady();
            chatBridge = true;
            Rusbik.config.setRunning(true);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (chatBridge){
            if (event.getAuthor().isBot()) return;

            if (event.getMessage().getContentDisplay().equals("")) return;

            if (event.getMessage().getContentRaw().equals("")) return;

            if (event.getMessage().getContentRaw().equals("!online")) {
                DiscordCommands.onlineCommand(event, server);
            }

            else if (event.getMessage().getContentRaw().startsWith("!add ")) {  // Añadir a la whitelist
                DiscordCommands.addCommand(event, server);
            }

            else if (event.getMessage().getContentRaw().startsWith("!remove ")) {  // Eliminar de la whitelist
                DiscordCommands.removeCommand(event, server);
            }

            else if (event.getMessage().getContentRaw().startsWith("!ban ")) {  // Banear de mc y base de datos.
                DiscordCommands.banCommand(event, server);
            }

            else if (event.getMessage().getContentRaw().startsWith("!pardon ")) {  // Desbanear.
                DiscordCommands.pardonCommand(event, server);
            }

            else if (event.getMessage().getContentRaw().equals("!reload")) {  // Recargar la whitelist y archivo de configuración.
                DiscordCommands.reloadCommand(event, server);
            }

            else if (event.getMessage().getContentRaw().equals("!list")) {  // Listar gente en la whitelist.
                DiscordCommands.listCommand(event, server);
            }

            else if (event.getChannel().getIdLong() == (Rusbik.config.chatChannelId)) {
                DiscordUtils.sendMessage(event, server);
            }
        }
    }

    public static void sendMessage(String msg) {
        if (chatBridge){
            try {
                TextChannel ch = jda.getTextChannelById(channelId);
                if (ch != null) ch.sendMessage(msg).queue();
            }
            catch (Exception e){
                System.out.println("wrong channelId :(");
            }
        }
    }

    public static void stop() {
        jda.shutdownNow();
        chatBridge = false;
    }
}
