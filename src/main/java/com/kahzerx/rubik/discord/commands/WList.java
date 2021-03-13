package com.kahzerx.rubik.discord.commands;

import com.kahzerx.rubik.discord.utils.DiscordPermission;
import com.kahzerx.rubik.discord.utils.DiscordUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;

public class WList extends Commands {
    public WList() {
        super.setCBody("list");
        super.setPermission(DiscordPermission.WHITELIST_CHAT);
    }

    @Override
    public void execute(final MessageReceivedEvent event, final MinecraftServer server) {
        if (DiscordUtils.isAllowed(this.getPermission(), event.getChannel().getIdLong())) {
            String[] names = server.getPlayerManager().getWhitelistedNames();
            if (names.length == 0) {
                event.getChannel().sendMessage("Whitelist is empty").queue();
            } else {
                StringBuilder msg = new StringBuilder("`");
                final int maxLength = 1500;
                for (int i = 0; i < names.length - 1; i++) {
                    msg.append(names[i]);
                    if (msg.length() < maxLength) {
                        msg.append(", ");
                    } else {
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
