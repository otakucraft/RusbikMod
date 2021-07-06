package com.kahzerx.rubik.discord.commands;

import com.kahzerx.rubik.discord.utils.DiscordPermission;
import com.kahzerx.rubik.discord.utils.DiscordUtils;
import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import com.kahzerx.rubik.Rusbik;
import com.kahzerx.rubik.database.RusbikDatabase;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class Exadd extends Commands {
    public Exadd() {
        super.setCBody("exadd");
        super.setPermission(DiscordPermission.ADMIN_CHAT);
    }

    @Override
    public void execute(final MessageReceivedEvent event, final MinecraftServer server) {
        if (DiscordUtils.isAllowed(this.getPermission(), event.getChannel().getIdLong())) {
            String[] req = event.getMessage().getContentRaw().split(" ");
            String playerName = req[1];

            if (req.length != 2) {  // El comando es !exadd Kahzerx.
                event.getChannel().sendMessage("!exadd <playerName>").queue();
                return;
            }

            Optional<GameProfile> gameProfile = server.getUserCache().findByName(playerName);

            if (gameProfile.isEmpty()) {  // El Jugador es premium.
                event.getChannel().sendMessage("No es premium :P").queue();
                return;
            }

            Whitelist whitelist = server.getPlayerManager().getWhitelist();

            if (whitelist.isAllowed(gameProfile.get())) {  // Si ya estaba en la whitelist.
                event.getChannel().sendMessage("Ya estaba en whitelist").queue();
                return;
            }

            WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile.get());
            final long id = 999999L;

            try {
                RusbikDatabase.addPlayerInformation(gameProfile.get().getName(), id);  // Añadir a la base de datos
                whitelist.add(whitelistEntry);  // Añadir a la whitelist vanilla.

                event.getChannel().sendMessage("Añadido :)").queue();

                if (Rusbik.config.getDiscordRole() != 0) {  // Dar rol de discord.
                    Guild guild = event.getGuild();
                    Role role = guild.getRoleById(Rusbik.config.getDiscordRole());
                    assert role != null;
                    guild.addRoleToMember(Objects.requireNonNull(event.getMember()), role).queue();
                }

            } catch (SQLException throwables) {
                whitelist.remove(whitelistEntry);
                event.getChannel().sendMessage("RIP :(, algo falló.").queue();
                throwables.printStackTrace();
            }
        }
    }
}
