package rusbik.discord.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;
import rusbik.Rusbik;
import rusbik.discord.commands.*;

import javax.annotation.Nonnull;
import java.util.List;

public class DiscordListener extends ListenerAdapter {

    private static JDA jda = null;
    public static String channelId = "";
    public static String token = "";
    public static boolean chatBridge = false;

    private static MinecraftServer server;

    public DiscordListener (MinecraftServer s){
        server = s;
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
        if (chatBridge) {
            if (event.getAuthor().isBot()) return;

            if (event.getMessage().getContentDisplay().equals("")) return;

            if (event.getMessage().getContentRaw().equals("")) return;

            Commands online = new Online();
            Commands add = new Add();
            Commands remove = new Remove();
            Commands exadd = new Exadd();
            Commands exremove = new Exremove();
            Commands ban = new Ban();
            Commands pardon = new Pardon();
            Commands reload = new Reload();
            Commands list = new WList();

            String prefix = "!";

            if (event.getMessage().getContentRaw().equals(prefix + online.getCBody())) {
                online.execute(event, server);
            }

            else if (event.getMessage().getContentRaw().startsWith(prefix + add.getCBody() + " ")) {  // Añadir a la whitelist
                add.execute(event, server);
            }

            else if (event.getMessage().getContentRaw().startsWith(prefix + remove.getCBody() + " ")) {  // Eliminar de la whitelist
                remove.execute(event, server);
            }

            else if (event.getMessage().getContentRaw().startsWith(prefix + exadd.getCBody() + " ")) {  // Añadir a la whitelist como excepción.
                exadd.execute(event, server);
            }

            else if (event.getMessage().getContentRaw().startsWith(prefix + exremove.getCBody() + " ")) {  // Eliminar de la whitelist de un jugador añadido como excepción.
                exremove.execute(event, server);
            }

            else if (event.getMessage().getContentRaw().startsWith(prefix + ban.getCBody() + " ")) {  // Banear de mc y base de datos.
                ban.execute(event, server);
            }

            else if (event.getMessage().getContentRaw().startsWith(prefix + pardon.getCBody() + " ")) {  // Desbanear.
                pardon.execute(event, server);
            }

            else if (event.getMessage().getContentRaw().equals(prefix + reload.getCBody())) {  // Recargar la whitelist y archivo de configuración.
                reload.execute(event, server);
            }

            else if (event.getMessage().getContentRaw().equals(prefix + list.getCBody())) {  // Listar gente en la whitelist.
                list.execute(event, server);
            }

            else if (event.getChannel().getIdLong() == (Rusbik.config.getChatChannelId())) {
                DiscordUtils.sendMessage(event, server);
            }
        }
    }

    public static void sendMessage(String msg) {
        if (chatBridge) {
            try {
                TextChannel ch = jda.getTextChannelById(channelId);
                if (ch != null) ch.sendMessage(msg).queue();
            }
            catch (Exception e){
                System.out.println("wrong channelId :(");
            }
        }
    }

    public static void sendAdminMessage(String msg) {
        if (chatBridge) {
            try {
                TextChannel ch = jda.getTextChannelById(Rusbik.config.getAdminChat().get(0));
                if (ch != null) ch.sendMessage(msg).queue();
            }
            catch (Exception e) {
                System.out.println("wrong channelId :(");
            }
        }
    }

    public static void stop() {
        jda.shutdownNow();
        chatBridge = false;
    }

    public static void checkSub(List<Long> ids) {
        assert jda != null;
        Thread dbCheck = new SubCheckThread("discordSubCheckThread", jda, ids, server);
        dbCheck.start();
    }
}
