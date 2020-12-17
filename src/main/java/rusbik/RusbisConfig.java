package rusbik;

import rusbik.discord.DiscordFileManager;

import java.util.List;

public class RusbisConfig {
    public RusbisConfig(String discordToken, long chatChannelId, boolean isRunning, List<Long> whitelistChat, List<Long> allowedChat, List<Long> adminChat) {
        this.discordToken = discordToken;
        this.chatChannelId = chatChannelId;
        this.isRunning = isRunning;
        this.whitelistChat = whitelistChat;
        this.allowedChat = allowedChat;
        this.adminChat = adminChat;
    }

    public RusbisConfig() {}

    public String discordToken;
    public long chatChannelId;
    public boolean isRunning;
    public List<Long> whitelistChat;
    public List<Long> allowedChat;
    public List<Long> adminChat;

    public void setDiscordToken(String discordToken) {
        this.discordToken = discordToken;
        DiscordFileManager.updateFile();
    }

    public void setChatChannelId(long chatChannelId) {
        this.chatChannelId = chatChannelId;
        DiscordFileManager.updateFile();
    }

    public void setRunning(boolean running) {
        isRunning = running;
        DiscordFileManager.updateFile();
    }

    @Override
    public String toString() {
        return "RusbisConfig{" +
                "discordToken='" + discordToken + '\'' +
                ", chatChannelId=" + chatChannelId +
                ", isRunning=" + isRunning +
                '}';
    }
}
