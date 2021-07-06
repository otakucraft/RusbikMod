package com.kahzerx.rubik.discord.commands;

import com.kahzerx.rubik.discord.utils.DiscordPermission;
import com.kahzerx.rubik.discord.utils.DiscordUtils;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import com.kahzerx.rubik.database.RusbikDatabase;

import java.sql.SQLException;
import java.util.Optional;

public class Ban extends Commands {
    public Ban() {
        super.setCBody("ban");
        super.setPermission(DiscordPermission.ADMIN_CHAT);
    }

    @Override
    public void execute(final MessageReceivedEvent event, final MinecraftServer server) {
        if (DiscordUtils.isAllowed(this.getPermission(), event.getChannel().getIdLong())) {
            String[] req = event.getMessage().getContentRaw().split(" ");
            String playerName = req[1];

            if (req.length != 2) {  // El comando es !ban Kahzerx.
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

                if (RusbikDatabase.isBanned(id)) {
                    event.getChannel().sendMessage("Ya estaba baneado.").queue();
                    return;
                }

                BannedPlayerList list = server.getPlayerManager().getUserBanList();

                if (!list.contains(gameProfile.get())) {  // Vanilla ban

                    BannedPlayerEntry playerEntry = new BannedPlayerEntry(gameProfile.get(), null, "DiscordBan", null, null);
                    list.add(playerEntry);

                    ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(gameProfile.get().getId());  // kickear si está conectado.
                    if (serverPlayerEntity != null) {
                        serverPlayerEntity.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.banned"));
                    }
                }

                RusbikDatabase.banUser(id);

                event.getChannel().sendMessage("Baneado!").queue();
            } catch (SQLException e) {
                event.getChannel().sendMessage("Ooops, error en la base de datos.").queue();
            }
        }
    }
}
