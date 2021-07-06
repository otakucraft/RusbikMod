package com.kahzerx.rubik.helpers;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import com.kahzerx.rubik.Rusbik;
import com.kahzerx.rubik.utils.KrusbibUtils;

import java.sql.SQLException;

public final class BackManager {
    private BackManager() { }

    public static void tpDeathPos(
            final ServerPlayerEntity player) throws SQLException {
        if (!Rusbik.players.containsKey(player.getName().getString())) {
            player.sendMessage(new LiteralText("Error al ejecutar /back, prueba a conectarte de nuevo o contacta con un administrador."), false);
            return;
        }
        BackPos deathPos = Rusbik.players.get(player.getName().getString()).back.getBackPos();
        // Intentar hacer tp a tu última deathPos.
        if (deathPos.isValid()) {
            player.teleport(KrusbibUtils.getWorld(deathPos.dim, player), deathPos.x, deathPos.y, deathPos.z, player.getYaw(), player.getPitch());
        } else {
            player.sendMessage(new LiteralText("Aun no has muerto :("), false);
        }
    }
}
