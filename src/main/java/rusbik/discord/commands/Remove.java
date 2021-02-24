package rusbik.discord.commands;

import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import rusbik.Rusbik;
import rusbik.database.RusbikDatabase;
import rusbik.discord.utils.DiscordPermission;
import rusbik.discord.utils.DiscordUtils;

import java.sql.SQLException;
import java.util.Objects;

public class Remove extends Commands {
    public Remove() {
        super.setCBody("remove");
        super.setPermission(DiscordPermission.WHITELIST_CHAT);
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server) {
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
            GameProfile gameProfile = server.getUserCache().findByName(playerName);

            if (gameProfile == null) {  // El Jugador es premium.
                event.getChannel().sendMessage("No es premium :P").queue();
                return;
            }

            if (!whitelist.isAllowed(gameProfile)) {
                event.getChannel().sendMessage("No está en la whitelist").queue();
                return;
            }

            long id = Long.parseLong(event.getAuthor().getId());

            try {
                if (!RusbikDatabase.allowedToRemove(id, gameProfile.getName())) {
                    RusbikDatabase.removeData(gameProfile.getName());  // Eliminar discordID, home y deathPos.

                    WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);  // Sacar de la whitelist vanilla.
                    whitelist.remove(whitelistEntry);

                    ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(gameProfile.getId());
                    if (serverPlayerEntity != null) {
                        serverPlayerEntity.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.not_whitelisted"));  // kickear si está conectado.
                    }

                    event.getChannel().sendMessage("Eliminado ;(").queue();

                    if (Rusbik.config.getDiscordRole() != 0) {  // Quitar rol de discord.
                        event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(Rusbik.config.getDiscordRole()))).queue();
                    }
                } else {
                    event.getChannel().sendMessage("No tienes permiso para eliminar a este usuario").queue();
                }
            }
            catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
