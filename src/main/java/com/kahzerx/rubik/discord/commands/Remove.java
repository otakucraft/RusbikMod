package com.kahzerx.rubik.discord.commands;

import com.kahzerx.rubik.discord.utils.DiscordPermission;
import com.kahzerx.rubik.discord.utils.DiscordUtils;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import com.kahzerx.rubik.Rusbik;
import com.kahzerx.rubik.database.RusbikDatabase;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class Remove extends Commands {
    public Remove() {
        super.setCBody("remove");
        super.setPermission(DiscordPermission.WHITELIST_CHAT);
    }

    @Override
    public void execute(final MessageReceivedEvent event, final MinecraftServer server) {
        if (DiscordUtils.isAllowed(this.getPermission(), event.getChannel().getIdLong())) {
            try {  // Si está baneado no puede meter a otro jugador.
                if (RusbikDatabase.isBanned(event.getAuthor().getIdLong())) {
                    event.getChannel().sendMessage("No puedes ejecutar este comando.").queue();
                    return;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            String[] req = event.getMessage().getContentRaw().split(" ");
            String playerName = req[1];

            if (req.length != 2) {  // El comando es !remove Kahzerx.
                event.getChannel().sendMessage("!remove <playerName>").queue();
                return;
            }

            Whitelist whitelist = server.getPlayerManager().getWhitelist();
            Optional<GameProfile> gameProfile = server.getUserCache().findByName(playerName);

            if (gameProfile.isEmpty()) {  // El Jugador es premium.
                event.getChannel().sendMessage("No es premium :P").queue();
                return;
            }

            if (!whitelist.isAllowed(gameProfile.get())) {
                event.getChannel().sendMessage("No está en la whitelist").queue();
                return;
            }

            long id = Long.parseLong(event.getAuthor().getId());

            try {
                if (RusbikDatabase.allowedToRemove(id, gameProfile.get().getName())) {
                    RusbikDatabase.removeData(gameProfile.get().getName());  // Eliminar discordID, home y deathPos.

                    WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile.get());  // Sacar de la whitelist vanilla.
                    whitelist.remove(whitelistEntry);

                    ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(gameProfile.get().getId());
                    if (serverPlayerEntity != null) {
                        serverPlayerEntity.networkHandler.disconnect(new LiteralText("Ya no estás en la whitelist :("));  // kickear si está conectado.
                    }

                    event.getChannel().sendMessage("Eliminado ;(").queue();

                    if (Rusbik.config.getDiscordRole() != 0) {  // Quitar rol de discord.
                        event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(Rusbik.config.getDiscordRole()))).queue();
                    }
                } else {
                    event.getChannel().sendMessage("No tienes permiso para eliminar a este usuario").queue();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
