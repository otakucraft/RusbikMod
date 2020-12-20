package rusbik.settings;

import rusbik.utils.FileManager;

import java.util.List;

public class RusbisConfig {
    // Esquema del yaml.
    public RusbisConfig(String discordToken, long chatChannelId, boolean isRunning, long discordRole, List<Long> whitelistChat, List<Long> allowedChat, List<Long> adminChat, long groupID, List<Long> validRoles) {
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

    public RusbisConfig() {}  // El constructor vacio es necesario o hace epic crash.

    public String discordToken;
    public long chatChannelId;
    public boolean isRunning;
    public long discordRole;
    public long groupID;
    public List<Long> whitelistChat;
    public List<Long> allowedChat;
    public List<Long> adminChat;
    public List<Long> validRoles;

    // Configuro los setters para modificar información.

    public void setDiscordToken(String discordToken) {
        this.discordToken = discordToken;
        FileManager.updateFile();  // Actualiza el archivo con los datos nuevos.
    }

    public void setChatChannelId(long chatChannelId) {
        this.chatChannelId = chatChannelId;
        FileManager.updateFile();
    }

    public void setRunning(boolean running) {
        isRunning = running;
        FileManager.updateFile();
    }

    public void setAdminChat(List<Long> adminChat) {
        this.adminChat = adminChat;
    }

    public void setAllowedChat(List<Long> allowedChat) {
        this.allowedChat = allowedChat;
    }

    public void setDiscordRole(long discordRole) {
        this.discordRole = discordRole;
    }

    public void setWhitelistChat(List<Long> whitelistChat) {
        this.whitelistChat = whitelistChat;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public void setValidRoles(List<Long> validRoles) {
        this.validRoles = validRoles;
    }

    public String getDiscordToken() {
        return discordToken;
    }

    public List<Long> getAdminChat() {
        return adminChat;
    }

    public List<Long> getAllowedChat() {
        return allowedChat;
    }

    public List<Long> getWhitelistChat() {
        return whitelistChat;
    }

    public long getChatChannelId() {
        return chatChannelId;
    }

    public long getDiscordRole() {
        return discordRole;
    }

    public List<Long> getValidRoles() {
        return validRoles;
    }

    public long getGroupID() {
        return groupID;
    }

    @Override
    public String toString() {
        return "RusbisConfig{" +
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
