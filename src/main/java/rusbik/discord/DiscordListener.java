package rusbik.discord;

import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.network.MessageType;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import rusbik.perms.PermsFileManager;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordListener extends ListenerAdapter {
    private static Pattern url_patt = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");
    private static JDA process = null;
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
            DiscordFileManager.updateFile(false);
            process = new JDABuilder(token).addEventListeners(new DiscordListener(server)).build();
            process.awaitReady();
            chatBridge = true;
            DiscordFileManager.updateFile(true);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (chatBridge){
            if (event.getAuthor().isBot()) return;
            if (event.getMessage().getContentDisplay().equals("")) return;
            if (event.getMessage().getContentRaw().equals("")) return;
            if (event.getMessage().getContentRaw().equals("!online")){
                if (event.getChannel().getId().equals("730028309173370931") || event.getChannel().getId().equals("608960549845467155") || event.getChannel().getId().equals("730011967980306452")){
                    StringBuilder msg = new StringBuilder();
                    int n = server.getPlayerManager().getPlayerList().size();
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
                        msg.append(player.getName().getString().replace("_", "\\_")).append("\n");
                    }
                    event.getChannel().sendMessage(Objects.requireNonNull(generateEmbed(msg, n)).build()).queue();
                }
            }

            else if (event.getMessage().getContentRaw().startsWith("!miembro ")){
                if (event.getChannel().getId().equals("730028309173370931") || event.getChannel().getId().equals("608960549845467155") || event.getChannel().getId().equals("730011967980306452")){
                    String[] req = event.getMessage().getContentRaw().split(" ");
                    if (req.length == 2){
                        for (Team team : server.getScoreboard().getTeams()){
                            if (team.getName().equals("MIEMBRO")){
                                server.getScoreboard().addPlayerToTeam(req[1], team);
                                event.getChannel().sendMessage("Añadido :D").queue();
                            }
                        }
                    }
                    else event.getChannel().sendMessage("!miembro <playerName>").queue();
                }
            }

            else if (event.getMessage().getContentRaw().startsWith("!add ")){
                if (event.getChannel().getId().equals("730011967980306452")){
                    String[] req = event.getMessage().getContentRaw().split(" ");
                    if (req.length == 2){
                        Whitelist whitelist = server.getPlayerManager().getWhitelist();
                        GameProfile gameProfile = server.getUserCache().findByName(req[1]);
                        if (gameProfile != null){
                            if (!whitelist.isAllowed(gameProfile)){
                                WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
                                whitelist.add(whitelistEntry);
                                event.getChannel().sendMessage("Añadido :)").queue();
                            }
                            else event.getChannel().sendMessage("Ya estaba en whitelist").queue();
                        }
                        else event.getChannel().sendMessage("No es premium :P").queue();
                    }
                    else event.getChannel().sendMessage("!add <playerName>").queue();
                }
            }

            else if (event.getMessage().getContentRaw().startsWith("!remove ")){
                if (event.getChannel().getId().equals("730011967980306452")){
                    String[] req = event.getMessage().getContentRaw().split(" ");
                    if (req.length == 2){
                        Whitelist whitelist = server.getPlayerManager().getWhitelist();
                        GameProfile gameProfile = server.getUserCache().findByName(req[1]);
                        if (gameProfile != null){
                            if (whitelist.isAllowed(gameProfile)){
                                WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
                                whitelist.remove(whitelistEntry);
                                event.getChannel().sendMessage("Eliminado ;(").queue();
                            }
                            else event.getChannel().sendMessage("No está en la whitelist").queue();
                        }
                        else event.getChannel().sendMessage("No es premium :P").queue();
                    }
                    else event.getChannel().sendMessage("!remove <playerName>").queue();
                }
            }

            else if (event.getMessage().getContentRaw().equals("!reload")){
                if (event.getChannel().getId().equals("730011967980306452")) {
                    server.getPlayerManager().reloadWhitelist();
                    event.getChannel().sendMessage("Whitelist reloaded").queue();
                    server.kickNonWhitelistedPlayers(server.getCommandSource());
                }
            }

            else if (event.getMessage().getContentRaw().equals("!list")){
                if (event.getChannel().getId().equals("730011967980306452")) {
                    String[] names = server.getPlayerManager().getWhitelistedNames();
                    if (names.length == 0) {
                        event.getChannel().sendMessage("Whitelist is empty").queue();
                    } else {
                        StringBuilder msg = new StringBuilder("`");
                        for (int i = 0; i < names.length - 1; i++){
                            msg.append(names[i]);
                            if (msg.length() < 1500) msg.append(", ");
                            else {
                                event.getChannel().sendMessage(msg.append("`")).queue();
                                msg.setLength(0);
                                msg.append("`");
                            }
                        }
                        event.getChannel().sendMessage(msg.append(names[names.length - 1]).append("`")).queue();
                    }
                }
            }

            else if (event.getMessage().getContentRaw().startsWith("!give ")){
                if (event.getChannel().getId().equals("730011967980306452")){
                    String[] req = event.getMessage().getContentRaw().split(" ");
                    if (req.length == 3){
                        String player = req[1];
                        try {
                            int permsInt = Integer.parseInt(req[2]);
                            if (permsInt > 0 && permsInt < 4) event.getChannel().sendMessage(PermsFileManager.setPerm(player, permsInt)).queue();
                            else event.getChannel().sendMessage("Pls input an integer between 1 and 3").queue();
                        }
                        catch (Exception e){
                            event.getChannel().sendMessage("Pls input an integer between 1 and 3").queue();
                        }
                    }
                    else event.getChannel().sendMessage("How to: !give <playerName> <int 1 to 3>").queue();
                }
                else event.getChannel().sendMessage("You can't use this command here").queue();
            }

            else if (event.getChannel().getId().equals(channelId)){
                String msg = "[Discord] <" + event.getAuthor().getName() + "> " + event.getMessage().getContentDisplay();
                if (msg.length() >= 256) msg = msg.substring(0, 253) + "...";

                Matcher m = url_patt.matcher(msg);
                MutableText finalMsg = new LiteralText("");
                boolean hasUrl = false;
                int prev = 0;

                while (m.find()){
                    hasUrl = true;
                    Text text = new LiteralText(m.group(0)).styled((style -> style.withColor(Formatting.GRAY)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, m.group(0)))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Open URL")))));
                    finalMsg = finalMsg.append(new LiteralText(msg.substring(prev, m.start()))).append(text);
                    prev = m.end();
                }
                if (hasUrl) server.getPlayerManager().broadcastChatMessage(finalMsg.append(msg.substring(prev)), MessageType.CHAT, Util.NIL_UUID);
                else server.getPlayerManager().broadcastChatMessage(new LiteralText(msg), MessageType.CHAT, Util.NIL_UUID);
            }
        }
    }

    public static void sendMessage(String msg){
        if (chatBridge){
            try {
                TextChannel ch = process.getTextChannelById(channelId);
                if (ch != null) ch.sendMessage(msg).queue();
            }
            catch (Exception e){
                System.out.println("wrong channelId :(");
            }
        }
    }

    public static void stop(){
        process.shutdownNow();
        chatBridge = false;
    }

    public static EmbedBuilder generateEmbed(StringBuilder msg, int n) {
        try {
            final EmbedBuilder emb = new EmbedBuilder();
            emb.setColor(Color.decode("#2ECC71"));
            if (n > 1) emb.setDescription("**" + n + " jugadores conectados** \n\n" + msg.toString());
            else emb.setDescription(n == 0 ? "**No hay nadie online :(**" : "**" + n + " jugador conectado** \n\n" + msg.toString());
            return emb;
        } catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
