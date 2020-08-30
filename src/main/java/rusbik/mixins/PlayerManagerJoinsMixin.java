package rusbik.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rusbik.database.RusbikDatabase;
import rusbik.discord.DiscordListener;

import java.sql.SQLException;

@Mixin(PlayerManager.class)
public class PlayerManagerJoinsMixin {
    @Shadow @Final private MinecraftServer server;

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) throws SQLException {
        if (DiscordListener.chatBridge) DiscordListener.sendMessage(":arrow_right: **" + player.getName().getString().replace("_", "\\_") + " joined the game!**");
        if (!RusbikDatabase.playerExists(player.getName().getString())){
            for (Team team : server.getScoreboard().getTeams()){
                if (team.getName().equals("MIEMBRO")){
                    server.getScoreboard().addPlayerToTeam(player.getName().getString(), team);
                }
            }
        }
        RusbikDatabase.addPlayerInformation(player.getName().getString());
    }
}
