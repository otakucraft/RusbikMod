package rusbik.settings;

import rusbik.utils.FileManager;

import java.util.List;

public class RubiConfig {
    private String discordToken;
    private long chatChannelId;
    private boolean isRunning;
    private long discordRole;
    private long groupID;
    private List<Long> whitelistChat;
    private List<Long> allowedChat;
    private List<Long> adminChat;
    private List<Long> validRoles;

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

    public long getDiscordRole() {
        return discordRole;
    }

    public List<Long> getAdminChat() {
        return adminChat;
    }

    public List<Long> getWhitelistChat() {
        return whitelistChat;
    }

    public List<Long> getAllowedChat() {
        return allowedChat;
    }

    public long getGroupID() {
        return groupID;
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
