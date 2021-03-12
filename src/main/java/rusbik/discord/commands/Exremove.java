package rusbik.discord.commands;

import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import rusbik.Rusbik;
import rusbik.database.RusbikDatabase;
import rusbik.discord.utils.DiscordPermission;
import rusbik.discord.utils.DiscordUtils;

import java.sql.SQLException;
import java.util.Objects;

public class Exremove extends Commands {
    public Exremove() {
        super.setCBody("exremove");
        super.setPermission(DiscordPermission.ADMIN_CHAT);
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server) {
        if (DiscordUtils.isAllowed(this.getPermission(), event.getChannel().getIdLong())) {
            String[] req = event.getMessage().getContentRaw().split(" ");
            String playerName = req[1];

            if (req.length != 2) {  // El comando es !exremove Kahzerx.
                event.getChannel().sendMessage("!exremove <playerName>").queue();
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

            long id = 999999L;

            try {
                if (RusbikDatabase.allowedToRemove(id, gameProfile.getName())) {
                    RusbikDatabase.removeData(gameProfile.getName());  // Eliminar discordID, home y deathPos.

                    WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);  // Sacar de la whitelist vanilla.
                    whitelist.remove(whitelistEntry);

                    ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(gameProfile.getId());
                    if (serverPlayerEntity != null) {
                        serverPlayerEntity.networkHandler.disconnect(new LiteralText("Ya no estás en la whitelist :("));  // kickear si está conectado.
                    }

                    event.getChannel().sendMessage("Eliminado ;(").queue();

                    if (Rusbik.config.getDiscordRole() != 0) {  // Quitar rol de discord.
                        event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(Rusbik.config.getDiscordToken()))).queue();
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
