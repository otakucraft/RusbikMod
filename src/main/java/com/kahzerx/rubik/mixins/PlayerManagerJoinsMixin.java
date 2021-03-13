package com.kahzerx.rubik.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.kahzerx.rubik.Rusbik;

import java.sql.SQLException;

@Mixin(PlayerManager.class)
// Mixin para notificar por discord cuando un jugador se une.
public class PlayerManagerJoinsMixin {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) throws SQLException {
        Rusbik.onPlayerJoins(player);
    }
}
