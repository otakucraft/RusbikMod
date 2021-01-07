package rusbik.discord.commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.minecraft.server.MinecraftServer;
import rusbik.discord.utils.DiscordPermission;
import rusbik.discord.utils.DiscordUtils;

public class WList extends Commands {
    public WList() {
        super.setCBody("list");
        super.setPermission(DiscordPermission.WHITELIST_CHAT);
    }

    @Override
    public void execute(MessageReceivedEvent event, MinecraftServer server) {
        if (DiscordUtils.isAllowed(this.getPermission(), event.getChannel().getIdLong())) {
            String[] names = server.getPlayerManager().getWhitelistedNames();
            if (names.length == 0) {
                event.getChannel().sendMessage("Whitelist is empty").queue();
            } else {
                StringBuilder msg = new StringBuilder("`");
                for (int i = 0; i < names.length - 1; i++){
                    msg.append(names[i]);
                    if (msg.length() < 1500) msg.append(", ");
                    else {
                        event.getChannel().sendMessage(msg.append("`")).queue();
                        msg.setLength(0);
                        msg.append("`");
                    }
                }
                event.getChannel().sendMessage(msg.append(names[names.length - 1]).append("`")).queue();
            }
        }
    }
}
