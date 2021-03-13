package com.kahzerx.rubik.discord.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import com.kahzerx.rubik.Rusbik;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DiscordUtils {

    private DiscordUtils() { }
    // Pattern de URLs
    private static final Pattern PATTERN = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)");

    // Crear el embed de jugadores conectados.
    public static EmbedBuilder generateEmbed(final StringBuilder msg, final int n) {
        try {
            final EmbedBuilder emb = new EmbedBuilder();
            emb.setColor(n != 0 ? Color.decode("#2ECC71") : Color.decode("#d31b1e"));
            if (n > 1) {
                emb.setDescription("**" + n + " jugadores conectados** \n\n" + msg.toString());
            } else {
                emb.setDescription(n == 0 ? "**No hay nadie online :(**" : "**" + n + " jugador conectado** \n\n" + msg.toString());
            }
            return emb;
        } catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // Chats en los que están permitidos determinados determinados comandos.
    public static boolean isAllowed(final DiscordPermission permission, final long chatId) {
        boolean shouldWork = false;
        int id = permission.getId();
        switch (id) {
            case 0:
                shouldWork = Rusbik.config.getAdminChat().contains(chatId);
                break;
            case 1:
                shouldWork = Rusbik.config.getWhitelistChat().contains(chatId);
                break;
            case 2:
                shouldWork = Rusbik.config.getAllowedChat().contains(chatId);
                break;
            default:
        }
        return shouldWork;
    }

    public static void sendMessage(final MessageReceivedEvent event, final MinecraftServer server) {
        String msg = "[Discord] <" + event.getAuthor().getName() + "> " + event.getMessage().getContentDisplay();
        final int maxLength = 256;
        if (msg.length() >= maxLength) {
            msg = msg.substring(0, maxLength - 3) + "...";
        }

        Matcher m = PATTERN.matcher(msg);
        MutableText finalMsg = new LiteralText("");
        boolean hasUrl = false;
        int prev = 0;

        while (m.find()) {
            hasUrl = true;
            Text text = new LiteralText(m.group(0)).styled((style -> style.withColor(Formatting.GRAY)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, m.group(0)))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Open URL")))));
            finalMsg = finalMsg.append(new LiteralText(msg.substring(prev, m.start()))).append(text);
            prev = m.end();
        }
        if (hasUrl) {
            server.getPlayerManager().broadcastChatMessage(finalMsg.append(msg.substring(prev)), MessageType.CHAT, Util.NIL_UUID);
        } else {
            server.getPlayerManager().broadcastChatMessage(new LiteralText(msg), MessageType.CHAT, Util.NIL_UUID);
        }
    }
}
