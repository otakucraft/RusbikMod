package rusbik.discord.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import rusbik.discord.utils.DiscordPermission;
import rusbik.discord.utils.DiscordUtils;

import java.util.Objects;

public class Online extends Commands {
    public Online() {
        super.setCBody("online");
        super.setPermission(DiscordPermission.ALLOWED_CHAT);
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server) {
        if (DiscordUtils.isAllowed(this.getPermission(), event.getChannel().getIdLong())) {
            StringBuilder msg = new StringBuilder();
            int n = server.getPlayerManager().getPlayerList().size();
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                msg.append(player.getName().getString().replace("_", "\\_")).append("\n");
            }
            event.getChannel().sendMessage(Objects.requireNonNull(DiscordUtils.generateEmbed(msg, n)).build()).queue();
        }
    }
}
