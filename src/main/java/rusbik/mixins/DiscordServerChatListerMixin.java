package rusbik.mixins;

import java.sql.SQLException;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rusbik.Rusbik;

@Mixin(ServerPlayNetworkHandler.class)
// Mixin para redirigir el chat de minecraft a discord.
public class DiscordServerChatListerMixin {
    @Shadow
    public ServerPlayerEntity player;
    @Inject(method = "onGameMessage", at = @At("RETURN"))
    public void chatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        Rusbik.onChatMessage(player, packet.getChatMessage());
    }

    // Cuando un jugador se va.
    @Inject(method = "onDisconnected", at = @At("RETURN"))
    private void onPlayerLeft(Text reason, CallbackInfo ci) throws SQLException {
        Rusbik.onPlayerLeaves(player);
    }
}
