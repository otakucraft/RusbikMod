package com.kahzerx.rubik.mixins;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.kahzerx.rubik.Rusbik;

import java.sql.SQLException;
import java.util.function.BooleanSupplier;


/**
 * Mixin that initializes all processes.
 */
@Mixin(MinecraftServer.class)
public class RusbikServerRunMixin {
    @Shadow @Final protected LevelStorage.Session session;

    @Inject(method = "runServer", at = @At("HEAD"))
    public void run (CallbackInfo ci){
        Rusbik.onRunServer((MinecraftServer) (Object) this, session);
    }

    /**
     * Server shut down
     * Stops the bot and the database connection when shutting down the server.
     * Stops all threads
     */
    @Inject(method = "runServer", at = @At("RETURN"))
    public void stop (CallbackInfo ci) throws SQLException, InterruptedException {
        Rusbik.onStopServer();
    }

    /**
     * Server regular tasks
     */
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;saveAllPlayerData()V"))
    public void onSave(BooleanSupplier shouldKeepTicking, CallbackInfo ci) throws SQLException {
        Rusbik.onAutoSave();
    }
}
