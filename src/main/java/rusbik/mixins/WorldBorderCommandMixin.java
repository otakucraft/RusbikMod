package rusbik.mixins;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.WorldBorderCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rusbik.Rusbik;

@Mixin(WorldBorderCommand.class)
// Mixin para registrar comandos custom.
public class WorldBorderCommandMixin {
    @Inject(method = "register", at = @At("RETURN"))
    private static void registerRusbik(CommandDispatcher<ServerCommandSource> dispatcher, CallbackInfo ci){
        Rusbik.registerCommand(dispatcher);
    }
}
