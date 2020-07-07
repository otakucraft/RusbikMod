package rusbik.mixins;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rusbik.RusbikFileManager;

@Mixin(MinecraftServer.class)
public class RusbikServerRunMixin {
    @Inject(method = "method_29741", at = @At("HEAD"))
    public void run (CallbackInfo ci){
        RusbikFileManager.tryCreatePlayerFile();
    }
}
