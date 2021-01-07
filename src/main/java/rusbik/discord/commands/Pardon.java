package rusbik.discord.commands;

import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.MinecraftServer;
import rusbik.database.RusbikDatabase;
import rusbik.discord.utils.DiscordPermission;
import rusbik.discord.utils.DiscordUtils;

import java.sql.SQLException;

public class Pardon extends Commands {
    public Pardon() {
        super.setCBody("pardon");
        super.setPermission(DiscordPermission.ADMIN_CHAT);
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server) {
        if (DiscordUtils.isAllowed(this.getPermission(), event.getChannel().getIdLong())) {
            String[] req = event.getMessage().getContentRaw().split(" ");
            String playerName = req[1];

            if (req.length != 2) {  // El comando es !pardon Kahzerx.
                event.getChannel().sendMessage("!ban <playerName>").queue();
                return;
            }

            GameProfile gameProfile = server.getUserCache().findByName(playerName);

            if (gameProfile == null) {  // El Jugador tiene que ser premium.
                event.getChannel().sendMessage("Este usuario no existe!").queue();
                return;
            }

            try {
                if (!RusbikDatabase.userExists(gameProfile.getName())) {
                    event.getChannel().sendMessage("Este usuario no existe!").queue();
                    return;
                }

                long id = RusbikDatabase.getID(gameProfile.getName());

                if (!RusbikDatabase.isBanned(id)) {
                    event.getChannel().sendMessage("No estaba baneado.").queue();
                    return;
                }

                BannedPlayerList list = server.getPlayerManager().getUserBanList();

                if (list.contains(gameProfile)) {  // Vanilla pardon
                    list.remove(gameProfile);
                }

                RusbikDatabase.pardonUser(id);
                RusbikDatabase.removeData(gameProfile.getName());
                event.getChannel().sendMessage("Desbaneado!").queue();
            }
            catch (SQLException e) {
                event.getChannel().sendMessage("Ooops, error en la base de datos.").queue();
            }
        }
    }
}
