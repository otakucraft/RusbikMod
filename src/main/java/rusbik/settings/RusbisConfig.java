package rusbik.settings;

import rusbik.utils.FileManager;

import java.util.List;

public class RusbisConfig {
    // Esquema del yaml.
    public RusbisConfig(String discordToken, long chatChannelId, boolean isRunning, long discordRole, List<Long> whitelistChat, List<Long> allowedChat, List<Long> adminChat) {
        this.discordToken = discordToken;
        this.chatChannelId = chatChannelId;
        this.isRunning = isRunning;
        this.discordRole = discordRole;
        this.whitelistChat = whitelistChat;
        this.allowedChat = allowedChat;
        this.adminChat = adminChat;
    }

    public RusbisConfig() {}  // El constructor vacio es necesario o hace epic crash.

    public String discordToken;
    public long chatChannelId;
    public boolean isRunning;
    public long discordRole;
    public List<Long> whitelistChat;
    public List<Long> allowedChat;
    public List<Long> adminChat;

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

    @Override
    public String toString() {
        return "RusbisConfig{" +
                "discordToken='" + discordToken + '\'' +
                ", chatChannelId=" + chatChannelId +
                ", isRunning=" + isRunning +
                ", discordRole=" + discordRole +
                ", whitelistChat=" + whitelistChat +
                ", allowedChat=" + allowedChat +
                ", adminChat=" + adminChat +
                '}';
    }
}
