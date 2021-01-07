package rusbik.discord.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import rusbik.discord.utils.DiscordPermission;
import rusbik.discord.utils.DiscordUtils;
import rusbik.utils.FileManager;

public class Reload extends Commands {
    public Reload() {
        super.setCBody("reload");
        super.setPermission(DiscordPermission.ADMIN_CHAT);
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server) {
        if (DiscordUtils.isAllowed(this.getPermission(), event.getChannel().getIdLong())) {
            server.getPlayerManager().reloadWhitelist();
            server.kickNonWhitelistedPlayers(server.getCommandSource());
            FileManager.initializeYaml();
            event.getChannel().sendMessage("Reloaded!").queue();
        }
    }
}
