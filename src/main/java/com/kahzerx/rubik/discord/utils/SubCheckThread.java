package com.kahzerx.rubik.discord.utils;

import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Whitelist;
import net.minecraft.server.WhitelistEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import com.kahzerx.rubik.Rusbik;
import com.kahzerx.rubik.database.RusbikDatabase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

public class SubCheckThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();
    private final JDA jda;
    private final List<Long> ids;

    MinecraftServer server;

    public SubCheckThread(final String name, final JDA jda, final List<Long> ids, final MinecraftServer s) {
        super(name);
        this.jda = jda;
        this.ids = ids;
        this.server = s;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Starting Subscription check");
            List<Member> members = new ArrayList<>();
            List<Long> tempList = new ArrayList<>();

            final int maxMemberRetrieve = 90;
            for (long id : ids) {
                tempList.add(id);
                if (tempList.size() > maxMemberRetrieve) {
                    members.addAll(Objects.requireNonNull(jda.getGuildById(Rusbik.config.getGroupID())).retrieveMembersByIds(tempList).get());
                    tempList.clear();
                }
            }
            members.addAll(Objects.requireNonNull(jda.getGuildById(Rusbik.config.getGroupID())).retrieveMembersByIds(tempList).get());

            List<Long> currentIDs = new ArrayList<>();

            for (Member member : members) {
                currentIDs.add(member.getIdLong());
                if (!hasValidRole(member.getRoles())) {  // Users a los que se les ha acabado la sub.
                    try {
                        if (Rusbik.config.getDiscordRole() != 0) {  // Eliminar rol de discord.
                            Guild guild = jda.getGuildById(Rusbik.config.getGroupID());
                            if (guild != null) {
                                Role role = guild.getRoleById(Rusbik.config.getDiscordRole());
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
            LOGGER.info("Finished Subscription check");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean hasValidRole(final List<Role> roles) {
        for (Role role : roles) {
            if (Rusbik.config.getValidRoles().contains(role.getIdLong())) {
                return true;
            }
        }
        return false;
    }

    private void removeFromWhitelist(final String playerName) {
        LOGGER.info("Starting Whitelist removal.");
        Whitelist whitelist = server.getPlayerManager().getWhitelist();

        Optional<GameProfile> gameProfile = server.getUserCache().findByName(playerName);

        if (gameProfile.isEmpty()) {  // TODO Esto no se qué he hecho
            return;
        }

        if (!whitelist.isAllowed(gameProfile.get())) {  // Comprobar si está en whitelist.
            DiscordListener.sendAdminMessage(String.format("No ha sido posible sacar a %s de la whitelist.", playerName));
            return;
        }

        WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile.get());  // Eliminar de la whitelist.
        whitelist.remove(whitelistEntry);

        ServerPlayerEntity serverPlayerEntity = server.getPlayerManager().getPlayer(gameProfile.get().getId());
        if (serverPlayerEntity != null) {
            serverPlayerEntity.networkHandler.disconnect(new LiteralText("F sub :("));  // kickear si está conectado.
        }

        DiscordListener.sendAdminMessage(String.format("A %s se le acabó la sub, F.", playerName));
        LOGGER.info("Finished Whitelist removal.");
    }

    private void syncWhitelist() throws SQLException {
        LOGGER.info("Starting whitelist/database sync.");
        List<String> nameList = RusbikDatabase.getNames();
        Whitelist whitelist = server.getPlayerManager().getWhitelist();
        List<String> actualWhitelist = Arrays.asList(whitelist.getNames());
        for (String name : nameList) {
            if (!actualWhitelist.contains(name)) {  // Añadir a los que están en la base de datos pero no en whitelist.
                Optional<GameProfile> gameProfile = server.getUserCache().findByName(name);
                if (gameProfile.isEmpty()) {  // El Jugador es premium.
                    RusbikDatabase.removeData(name);
                    continue;
                }

                WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile.get());
                whitelist.add(whitelistEntry);
                DiscordListener.sendAdminMessage(String.format("%s añadido a la whitelist.", name));
            }
        }

        server.getPlayerManager().reloadWhitelist();

        whitelist = server.getPlayerManager().getWhitelist();
        actualWhitelist = Arrays.asList(whitelist.getNames());

        for (String player : actualWhitelist) {  // Sacar a los que están en whitelist pero no en la base de datos.
            if (!nameList.contains(player)) {
                Optional<GameProfile> gameProfile = server.getUserCache().findByName(player);
                if (gameProfile.isEmpty()) {
                    continue;
                }
                WhitelistEntry whitelistEntry = new WhitelistEntry(gameProfile.get());
                whitelist.remove(whitelistEntry);
                DiscordListener.sendAdminMessage(String.format("%s eliminado de la whitelist.", player));
            }
        }

        server.getPlayerManager().reloadWhitelist();

        LOGGER.info("Finished whitelist/database sync.");
    }
}
