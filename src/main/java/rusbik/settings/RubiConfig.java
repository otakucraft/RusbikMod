package rusbik.settings;

import rusbik.utils.FileManager;

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

    public RubiConfig(String discordToken, long chatChannelId, boolean isRunning, long discordRole, List<Long> whitelistChat, List<Long> allowedChat, List<Long> adminChat, long groupID, List<Long> validRoles) {
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

    public void setDiscordToken(String discordToken) {
        this.discordToken = discordToken;
        FileManager.updateFile();
    }

    public String getDiscordToken() {
        return discordToken;
    }

    public void setChatChannelId(long chatChannelId) {
        this.chatChannelId = chatChannelId;
        FileManager.updateFile();
    }

    public long getChatChannelId() {
        return chatChannelId;
    }

    public void setRunning(boolean running) {
        isRunning = running;
        FileManager.updateFile();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setDiscordRole(long discordRole) {
        this.discordRole = discordRole;
        FileManager.updateFile();
    }

    public long getDiscordRole() {
        return discordRole;
    }

    public void addAdminChat(long adminChat) {
        this.adminChat.add(adminChat);
        FileManager.updateFile();
    }

    public void removeAdminChat(long adminChat) {
        this.adminChat.remove(adminChat);
        FileManager.updateFile();
    }

    public List<Long> getAdminChat() {
        return adminChat;
    }

    public void addWhitelistChat(long whitelistChat) {
        this.whitelistChat.add(whitelistChat);
        FileManager.updateFile();
    }

    public void removeWhitelistChat(long whitelistChat) {
        this.whitelistChat.remove(whitelistChat);
        FileManager.updateFile();
    }

    public List<Long> getWhitelistChat() {
        return whitelistChat;
    }

    public void addAllowedChat(long allowedChat) {
        this.allowedChat.add(allowedChat);
        FileManager.updateFile();
    }

    public void removeAllowedChat(long allowedChat) {
        this.allowedChat.remove(allowedChat);
        FileManager.updateFile();
    }

    public List<Long> getAllowedChat() {
        return allowedChat;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
        FileManager.updateFile();
    }

    public long getGroupID() {
        return groupID;
    }

    public void addValidRoles(long validRoles) {
        this.validRoles.add(validRoles);
        FileManager.updateFile();
    }

    public void removeValidRoles(long validRoles) {
        this.validRoles.remove(validRoles);
        FileManager.updateFile();
    }

    public List<Long> getValidRoles() {
        return validRoles;
    }

    @Override
    public String toString() {
        return "RubiConfig{" +
                "discordToken='" + discordToken + '\'' +
                ", chatChannelId=" + chatChannelId +
                ", isRunning=" + isRunning +
                ", discordRole=" + discordRole +
                ", groupID=" + groupID +
                ", whitelistChat=" + whitelistChat +
                ", allowedChat=" + allowedChat +
                ", adminChat=" + adminChat +
                ", validRoles=" + validRoles +
                '}';
    }
}
