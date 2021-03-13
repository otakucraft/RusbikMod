package com.kahzerx.rubik.settings;

import com.kahzerx.rubik.utils.FileManager;

import java.util.List;

public class RubiConfig {
    private String discordToken;
    private long chatChannelId;
    private boolean isRunning;
    private long discordRole;
    private long groupID;
    private final List<Long> whitelistChat;
    private final List<Long> allowedChat;
    private final List<Long> adminChat;
    private final List<Long> validRoles;

    public RubiConfig(final String discordToken,
                      final long chatChannelId,
                      final boolean isRunning,
                      final long discordRole,
                      final List<Long> whitelistChat,
                      final List<Long> allowedChat,
                      final List<Long> adminChat,
                      final long groupID,
                      final List<Long> validRoles) {
        this.discordToken = discordToken;
        this.chatChannelId = chatChannelId;
        this.isRunning = isRunning;
        this.groupID = groupID;
        this.discordRole = discordRole;
        this.whitelistChat = whitelistChat;
        this.allowedChat = allowedChat;
        this.adminChat = adminChat;
        this.validRoles = validRoles;
    }

    public void setDiscordToken(final String discordToken) {
        this.discordToken = discordToken;
        FileManager.updateFile();
    }

    public String getDiscordToken() {
        return discordToken;
    }

    public void setChatChannelId(final long chatChannelId) {
        this.chatChannelId = chatChannelId;
        FileManager.updateFile();
    }

    public long getChatChannelId() {
        return chatChannelId;
    }

    public void setRunning(final boolean running) {
        isRunning = running;
        FileManager.updateFile();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setDiscordRole(final long discordRole) {
        this.discordRole = discordRole;
        FileManager.updateFile();
    }

    public long getDiscordRole() {
        return discordRole;
    }

    public void addAdminChat(final long adminChat) {
        this.adminChat.add(adminChat);
        FileManager.updateFile();
    }

    public void removeAdminChat(final long adminChat) {
        this.adminChat.remove(adminChat);
        FileManager.updateFile();
    }

    public List<Long> getAdminChat() {
        return adminChat;
    }

    public void addWhitelistChat(final long whitelistChat) {
        this.whitelistChat.add(whitelistChat);
        FileManager.updateFile();
    }

    public void removeWhitelistChat(final long whitelistChat) {
        this.whitelistChat.remove(whitelistChat);
        FileManager.updateFile();
    }

    public List<Long> getWhitelistChat() {
        return whitelistChat;
    }

    public void addAllowedChat(final long allowedChat) {
        this.allowedChat.add(allowedChat);
        FileManager.updateFile();
    }

    public void removeAllowedChat(final long allowedChat) {
        this.allowedChat.remove(allowedChat);
        FileManager.updateFile();
    }

    public List<Long> getAllowedChat() {
        return allowedChat;
    }

    public void setGroupID(final long groupID) {
        this.groupID = groupID;
        FileManager.updateFile();
    }

    public long getGroupID() {
        return groupID;
    }

    public void addValidRoles(final long validRoles) {
        this.validRoles.add(validRoles);
        FileManager.updateFile();
    }

    public void removeValidRoles(final long validRoles) {
        this.validRoles.remove(validRoles);
        FileManager.updateFile();
    }

    public List<Long> getValidRoles() {
        return validRoles;
    }

    @Override
    public String toString() {
        return "RubiConfig{"
                + "discordToken='" + discordToken + '\''
                + ", chatChannelId=" + chatChannelId
                + ", isRunning=" + isRunning
                + ", discordRole=" + discordRole
                + ", groupID=" + groupID
                + ", whitelistChat=" + whitelistChat
                + ", allowedChat=" + allowedChat
                + ", adminChat=" + adminChat
                + ", validRoles=" + validRoles
                + '}';
    }
}
