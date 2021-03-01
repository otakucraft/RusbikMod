package rusbik.helpers;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import rusbik.Rusbik;
import rusbik.utils.KrusbibUtils;

import java.sql.SQLException;

public class BackManager {
    public static void tpDeathPos(ServerPlayerEntity player) throws SQLException {
        if (!Rusbik.players.containsKey(player.getName().getString())) {
            player.sendMessage(new LiteralText("Error al ejecutar /back, prueba a conectarte de nuevo o contacta con un administrador."), false);
            return;
        }
        BackPos deathPos = Rusbik.players.get(player.getName().getString()).back.getBackPos();
        // Intentar hacer tp a tu última deathPos.
        if (deathPos.isValid()) {
            player.teleport(KrusbibUtils.getWorld(deathPos.dim, player), deathPos.X, deathPos.Y, deathPos.Z, player.yaw, player.pitch);
        }
        else{
            player.sendMessage(new LiteralText("Aun no has muerto :("), false);
        }
    }
}
