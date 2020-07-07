package rusbik.mixins;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.SeedCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CommandManager.class)
public class CommandManagerSeedMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/SeedCommand;register(Lcom/mojang/brigadier/CommandDispatcher;Z)V"))
    public void onRegister(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated){
        SeedCommand.register(dispatcher, false);
    }
}
