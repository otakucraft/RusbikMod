package com.kahzerx.rubik.discord.commands;

import com.kahzerx.rubik.discord.utils.DiscordPermission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;

public class Commands {
    private String cBody;
    private DiscordPermission permission;

    public Commands() { }

    public void execute(final MessageReceivedEvent event, final MinecraftServer server) { }

    public void setCBody(final String cBody) {
        this.cBody = cBody;
    }

    public String getCBody() {
        return cBody;
    }

    public void setPermission(final DiscordPermission permission) {
        this.permission = permission;
    }

    public DiscordPermission getPermission() {
        return permission;
    }
}
