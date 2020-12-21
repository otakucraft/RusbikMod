package rusbik.utils;

import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import rusbik.Rusbik;
import rusbik.database.RusbikDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DiscordCheckThread extends Thread {
    private final JDA jda;
    private final List<Long> ids;

    MinecraftServer server;

    public DiscordCheckThread(String name, JDA jda, List<Long> ids, MinecraftServer s) {
        super(name);
        this.jda = jda;
        this.ids = ids;
        this.server = s;
    }

    @Override
    public void run() {
        System.out.println("Discord User check - START");

        List<Member> members = Objects.requireNonNull(jda.getGuildById(Rusbik.config.groupID)).retrieveMembersByIds(ids).get();
        List<Long> currentIDs = new ArrayList<>();

        for (Member member : members) {
            currentIDs.add(member.getIdLong());
            if (!hasValidRole(member.getRoles())) {  // Users a los que se les ha acabado la sub.
                try {
                    if (Rusbik.config.discordRole != 0) {  // Eliminar rol de discord.
                        Guild guild = jda.getGuildById(Rusbik.config.groupID);
                        if (guild != null) {
                            Role role = guild.getRoleById(Rusbik.config.discordRole);
                            if (role != null) {
                                guild.removeRoleFromMember(member, role).queue();
                            }
                        }
                    }

                    String name = RusbikDatabase.getPlayerName(member.getIdLong());

                    if (name == null) {
                        continue;
                    }
                    RusbikDatabase.removeData(name);
                    removeFromWhitelist(name);

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        for (long id : ids) {  // La cosa de isMember no va, a veces da true y otras false, idk.
            if (!currentIDs.contains(id)) {  // Users que han dejado el server.
                try {
                    String name = RusbikDatabase.getPlayerName(id);
                    assert name != null;
                    RusbikDatabase.removeData(name);
                    removeFromWhitelist(name);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        try {
            syncWhitelist();  // Sincronizar la base de datos con la whitelist
            // Hago esto porque hay una forma de bypassear el dejar de ser sub y mantener acceso cambiándote de nombre de mc.
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        DiscordListener.latch.countDown();
        System.out.println("Discord User check - END");
    }

    private boolean hasValidRole(List<Role> roles) {
        for (Role role : roles) {
            if (Rusbik.config.validRoles.contains(role.getIdLong())) {
                return true;
            }
        }
        return false;
    }

    private void removeFromWhitelist(String playerName) {
        Whitelist whitelist = server.getPlayerManager().getWhitelist();

        GameProfile gameProfile = server.getUserCache().findByName(playerName);

        if (!whitelist.isAllowed(gameProfile)) {  // Comprobar si está en whitelist.
            DiscordListener.sendAdminMessage(String.format("No ha sido posible sacar a %s de la whitelist.", playerName));
            return;
        }

        WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);  // Eliminar de la whitelist.
        whitelist.remove(whitelistEntry);

        assert gameProfile != null;

        ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(gameProfile.getId());
        if (serverPlayerEntity != null) {
            serverPlayerEntity.networkHandler.disconnect(new TranslatableText("multiplayer.disconnect.not_whitelisted"));  // kickear si está conectado.
        }

        DiscordListener.sendAdminMessage(String.format("A %s se le acabó la sub, F.", playerName));
    }

    private void syncWhitelist() throws SQLException {
        List<String> nameList = RusbikDatabase.getNames();
        System.out.println(nameList);
        Whitelist whitelist = server.getPlayerManager().getWhitelist();
        System.out.println(Arrays.toString(whitelist.getNames()));
        List<String> actualWhitelist = Arrays.asList(whitelist.getNames());
        for (String name : nameList) {
            if (!actualWhitelist.contains(name)) {  // Añadir a los que están en la base de datos pero no en whitelist.
                GameProfile gameProfile = server.getUserCache().findByName(name);
                if (gameProfile == null) {  // El Jugador es premium.
                    RusbikDatabase.removeData(name);
                    continue;
                }

                WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
                whitelist.add(whitelistEntry);
                DiscordListener.sendAdminMessage(String.format("%s añadido a la whitelist.", name));
            }
        }

        server.getPlayerManager().reloadWhitelist();

        whitelist = server.getPlayerManager().getWhitelist();
        actualWhitelist = Arrays.asList(whitelist.getNames());

        for (String player : actualWhitelist) {  // Sacar a los que están wn whitelist pero no en la base de datos.
            if (!nameList.contains(player)) {
                GameProfile gameProfile = server.getUserCache().findByName(player);
                WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile);
                whitelist.remove(whitelistEntry);
                DiscordListener.sendAdminMessage(String.format("%s eliminado de la whitelist.", player));
            }
        }

        server.getPlayerManager().reloadWhitelist();
    }
}
