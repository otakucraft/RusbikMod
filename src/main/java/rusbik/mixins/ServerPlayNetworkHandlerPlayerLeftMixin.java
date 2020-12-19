package rusbik.mixins;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rusbik.utils.DiscordListener;

@Mixin(ServerPlayNetworkHandler.class)
// Mixin para notificar por discord cuando un jugador se desconecta.
public class ServerPlayNetworkHandlerPlayerLeftMixin {
    @Shadow public ServerPlayerEntity player;
    @Inject(method = "onDisconnected", at = @At("RETURN"))
    private void onPlayerLeft(Text reason, CallbackInfo ci){
        if (DiscordListener.chatBridge) DiscordListener.sendMessage(":arrow_left: **" + player.getName().getString().replace("_", "\\_") + " left the game!**");
    }
}
