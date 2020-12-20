package rusbik.utils;

import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.JDA;
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
                    String name = RusbikDatabase.getPlayerName(member.getIdLong());
                    assert name != null;
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

        DiscordListener.sendAdminMessage(String.format("A %s se le acabó la sub :(", playerName));
    }
}
