package com.kahzerx.rubik.discord.commands;

import com.kahzerx.rubik.discord.utils.DiscordPermission;
import com.kahzerx.rubik.discord.utils.DiscordUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import com.kahzerx.rubik.utils.FileManager;

public class Reload extends Commands {
    public Reload() {
        super.setCBody("reload");
        super.setPermission(DiscordPermission.ADMIN_CHAT);
    }

    @Override
    public void execute(final MessageReceivedEvent event, final MinecraftServer server) {
        if (DiscordUtils.isAllowed(this.getPermission(), event.getChannel().getIdLong())) {
            server.getPlayerManager().reloadWhitelist();
            server.kickNonWhitelistedPlayers(server.getCommandSource());
            FileManager.initializeJson();
            event.getChannel().sendMessage("Reloaded!").queue();
        }
    }
}
