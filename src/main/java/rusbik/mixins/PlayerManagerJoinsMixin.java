package rusbik.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rusbik.RusbikFileManager;
import rusbik.discord.DiscordListener;
import rusbik.perms.Perms;

@Mixin(PlayerManager.class)
public class PlayerManagerJoinsMixin {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci){
        if (DiscordListener.chatBridge){
            DiscordListener.sendMessage(":arrow_right: **" + player.getName().getString().replace("_", "\\_") + " joined the game!**");
        }
        RusbikFileManager.onPlayerJoins(player);
        Perms.addToArray(player.getName().getString());
    }
}
