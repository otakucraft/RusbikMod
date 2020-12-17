package rusbik.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import rusbik.Rusbik;

import java.awt.*;

public class DiscordUtils {

    public static EmbedBuilder generateEmbed(StringBuilder msg, int n) {
        try {
            final EmbedBuilder emb = new EmbedBuilder();
            emb.setColor(n != 0 ? Color.decode("#2ECC71") : Color.decode("#d31b1e"));
            if (n > 1) emb.setDescription("**" + n + " jugadores conectados** \n\n" + msg.toString());
            else emb.setDescription(n == 0 ? "**No hay nadie online :(**" : "**" + n + " jugador conectado** \n\n" + msg.toString());
            return emb;
        } catch (final Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static boolean isAllowed(int id, long chatId) {
        boolean shouldWork = false;
        switch (id) {
            case 0:
                shouldWork = Rusbik.config.adminChat.contains(chatId);
                break;
            case 1:
                shouldWork = Rusbik.config.whitelistChat.contains(chatId);
                break;
            case 2:
                shouldWork = Rusbik.config.allowedChat.contains(chatId);
                break;
        }
        return shouldWork;
    }
}
