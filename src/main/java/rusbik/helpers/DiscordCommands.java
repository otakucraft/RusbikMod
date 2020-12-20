package rusbik.helpers;

import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import rusbik.Rusbik;
import rusbik.database.RusbikDatabase;
import rusbik.utils.DiscordUtils;
import rusbik.utils.FileManager;

import java.sql.SQLException;
import java.util.Objects;

public class DiscordCommands {
    public static void onlineCommand(MessageReceivedEvent event, MinecraftServer server) {
        if (DiscordUtils.isAllowed(2, event.getChannel().getIdLong())) {
            StringBuilder msg = new StringBuilder();
            int n = server.getPlayerManager().getPlayerList().size();
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                msg.append(player.getName().getString().replace("_", "\\_")).append("\n");
            }
            event.getChannel().sendMessage(Objects.requireNonNull(DiscordUtils.generateEmbed(msg, n)).build()).queue();
        }
    }

    public static void addCommand(MessageReceivedEvent event, MinecraftServer server) {
        if (DiscordUtils.isAllowed(1, event.getChannel().getIdLong())) {
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

            if (req.length != 2) {  // El comando es !add Kahzerx.
                event.getChannel().sendMessage("!add <playerName>").queue();
                return;
            }

            Whitelist whitelist = server.getPlayerManager().getWhitelist();
            GameProfile gameProfile = server.getUserCache().findByName(playerName);

            if (gameProfile == null) {  // El Jugador es premium.
                event.getChannel().sendMessage("No es premium :P").queue();
                return;
            }

            if (whitelist.isAllowed(gameProfile)) {  // Si ya estaba en la whitelist.
                event.getChannel().sendMessage("Ya estaba en whitelist").queue();
                return;
            }

            WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
            long id = event.getAuthor().getIdLong();

            try {
                if (RusbikDatabase.hasPlayer(id)) {
                    event.getChannel().sendMessage("Solo puedes meter en la whitelist a 1 persona").queue();
                    return;
                }

                RusbikDatabase.addPlayerInformation(playerName, id);  // Añadir a la base de datos
                whitelist.add(whitelistEntry);  // Añadir a la whitelist vanilla.

                event.getChannel().sendMessage("Añadido :)").queue();

                if (Rusbik.config.discordRole != 0) {  // Dar rol de discord.
                    Guild guild = event.getGuild();
                    Role role = guild.getRoleById(Rusbik.config.discordRole);
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

    public static void removeCommand(MessageReceivedEvent event, MinecraftServer server) {
        if (DiscordUtils.isAllowed(1, event.getChannel().getIdLong())) {
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
                if (!RusbikDatabase.allowedToRemove(id, playerName)) {
                    event.getChannel().sendMessage("No tienes permiso para eliminar a este usuario").queue();
                    return;
                }

                RusbikDatabase.removeData(playerName);  // Eliminar discordID, home y deathPos.

                WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);  // Sacar de la whitelist vanilla.
                whitelist.remove(whitelistEntry);

                ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(gameProfile.getId());
                if (serverPlayerEntity != null) {
                    serverPlayerEntity.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.not_whitelisted"));  // kickear si está conectado.
                }

                event.getChannel().sendMessage("Eliminado ;(").queue();

                if (Rusbik.config.discordRole != 0) {  // Quitar rol de discord.
                    event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(Rusbik.config.discordRole))).queue();
                }
            }
            catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static void banCommand(MessageReceivedEvent event, MinecraftServer server) {
        if (DiscordUtils.isAllowed(0, event.getChannel().getIdLong())) {
            String[] req = event.getMessage().getContentRaw().split(" ");
            String playerName = req[1];

            if (req.length != 2) {  // El comando es !ban Kahzerx.
                event.getChannel().sendMessage("!ban <playerName>").queue();
                return;
            }

            GameProfile gameProfile = server.getUserCache().findByName(playerName);

            if (gameProfile == null) {  // El Jugador tiene que ser premium.
                event.getChannel().sendMessage("Este usuario no existe!").queue();
                return;
            }

            try {
                if (!RusbikDatabase.userExists(playerName)) {
                    event.getChannel().sendMessage("Este usuario no existe!").queue();
                    return;
                }

                long id = RusbikDatabase.getID(playerName);

                if (RusbikDatabase.isBanned(id)) {
                    event.getChannel().sendMessage("Ya estaba baneado.").queue();
                    return;
                }

                BannedPlayerList list = server.getPlayerManager().getUserBanList();

                if (!list.contains(gameProfile)) {  // Vanilla ban

                    BannedPlayerEntry playerEntry = new BannedPlayerEntry(gameProfile, null, "DiscordBan", null, null);
                    list.add(playerEntry);

                    ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(gameProfile.getId());  // kickear si está conectado.
                    if (serverPlayerEntity != null) serverPlayerEntity.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.banned"));
                }

                RusbikDatabase.banUser(id);

                if (Rusbik.config.discordRole != 0) {  // Quitar rol de discord.
                    event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(Rusbik.config.discordRole))).queue();
                }

                event.getChannel().sendMessage("Baneado!").queue();
            }
            catch (SQLException e) {
                event.getChannel().sendMessage("Ooops, error en la base de datos.").queue();
            }
        }
    }

    public static void pardonCommand(MessageReceivedEvent event, MinecraftServer server) {
        if (DiscordUtils.isAllowed(0, event.getChannel().getIdLong())) {
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
                if (!RusbikDatabase.userExists(playerName)) {
                    event.getChannel().sendMessage("Este usuario no existe!").queue();
                    return;
                }

                long id = RusbikDatabase.getID(playerName);

                if (!RusbikDatabase.isBanned(id)) {
                    event.getChannel().sendMessage("No estaba baneado.").queue();
                    return;
                }

                BannedPlayerList list = server.getPlayerManager().getUserBanList();

                if (list.contains(gameProfile)) {  // Vanilla pardon
                    list.remove(gameProfile);
                }

                RusbikDatabase.pardonUser(id);
                event.getChannel().sendMessage("Desbaneado!").queue();
            }
            catch (SQLException e) {
                event.getChannel().sendMessage("Ooops, error en la base de datos.").queue();
            }
        }
    }

    public static void reloadCommand(MessageReceivedEvent event, MinecraftServer server) {
        if (DiscordUtils.isAllowed(0, event.getChannel().getIdLong())) {
            server.getPlayerManager().reloadWhitelist();
            server.kickNonWhitelistedPlayers(server.getCommandSource());
            FileManager.initializeYaml();
            event.getChannel().sendMessage("Reloaded!").queue();
        }
    }

    public static void listCommand(MessageReceivedEvent event, MinecraftServer server) {
        if (DiscordUtils.isAllowed(1, event.getChannel().getIdLong())) {
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
}
