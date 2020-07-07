package rusbik.mixins;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rusbik.discord.DiscordListener;

@Mixin(PlayerAdvancementTracker.class)
public class DiscordAdvancementMixin {
    @Shadow
    private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    public void onAdvancement(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir){
        if (DiscordListener.chatBridge){
            Text text = new TranslatableText("chat.type.advancement." + advancement.getDisplay().getFrame().getId(), new Object[]{this.owner.getDisplayName(), advancement.toHoverableText()});
            DiscordListener.sendMessage(":confetti_ball: **" + text.getString().replace("_", "\\_") + "**");
        }
    }
}
