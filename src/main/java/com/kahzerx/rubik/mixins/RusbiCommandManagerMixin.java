package com.kahzerx.rubik.mixins;

import com.kahzerx.rubik.Rusbik;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.SeedCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(CommandManager.class)
// Registro de comandos y habilitar el /seed para todos los jugadores.
public abstract class RusbiCommandManagerMixin {
    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

    @Shadow protected abstract void makeTreeForSource(CommandNode<ServerCommandSource> tree, CommandNode<CommandSource> result, ServerCommandSource source, Map<CommandNode<ServerCommandSource>, CommandNode<CommandSource>> resultNodes);

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/command/SeedCommand;register(Lcom/mojang/brigadier/CommandDispatcher;Z)V"))
    public void onRegister(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        SeedCommand.register(dispatcher, false);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void regCommands(CommandManager.RegistrationEnvironment environment, CallbackInfo ci) {
        Rusbik.registerCommands(dispatcher);
    }
}
