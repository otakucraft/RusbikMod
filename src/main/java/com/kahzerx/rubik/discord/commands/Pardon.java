package com.kahzerx.rubik.discord.commands;

import com.kahzerx.rubik.discord.utils.DiscordPermission;
import com.kahzerx.rubik.discord.utils.DiscordUtils;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.MinecraftServer;
import com.kahzerx.rubik.database.RusbikDatabase;

import java.sql.SQLException;
import java.util.Optional;

public class Pardon extends Commands {
    public Pardon() {
        super.setCBody("pardon");
        super.setPermission(DiscordPermission.ADMIN_CHAT);
    }

    @Override
    public void execute(final MessageReceivedEvent event, final MinecraftServer server) {
        if (DiscordUtils.isAllowed(this.getPermission(), event.getChannel().getIdLong())) {
            String[] req = event.getMessage().getContentRaw().split(" ");
            String playerName = req[1];

            if (req.length != 2) {  // El comando es !pardon Kahzerx.
                event.getChannel().sendMessage("!ban <playerName>").queue();
                return;
            }

            Optional<GameProfile> gameProfile = server.getUserCache().findByName(playerName);

            if (gameProfile.isEmpty()) {  // El Jugador tiene que ser premium.
                event.getChannel().sendMessage("Este usuario no existe!").queue();
                return;
            }

            try {
                if (!RusbikDatabase.userExists(gameProfile.get().getName())) {
                    event.getChannel().sendMessage("Este usuario no existe!").queue();
                    return;
                }

                long id = RusbikDatabase.getID(gameProfile.get().getName());

                if (!RusbikDatabase.isBanned(id)) {
                    event.getChannel().sendMessage("No estaba baneado.").queue();
                    return;
                }

                BannedPlayerList list = server.getPlayerManager().getUserBanList();

                if (list.contains(gameProfile.get())) {  // Vanilla pardon
                    list.remove(gameProfile.get());
                }

                RusbikDatabase.pardonUser(id);
                RusbikDatabase.removeData(gameProfile.get().getName());
                event.getChannel().sendMessage("Desbaneado!").queue();
            } catch (SQLException e) {
                event.getChannel().sendMessage("Ooops, error en la base de datos.").queue();
            }
        }
    }
}
