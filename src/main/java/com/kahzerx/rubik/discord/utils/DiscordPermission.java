package com.kahzerx.rubik.discord.utils;

public enum DiscordPermission {
    ADMIN_CHAT(0),
    WHITELIST_CHAT(1),
    ALLOWED_CHAT(2);

    private final int id;

    DiscordPermission(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
