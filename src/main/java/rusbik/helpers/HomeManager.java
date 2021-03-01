package rusbik.helpers;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import rusbik.Rusbik;
import rusbik.utils.KrusbibUtils;

public class HomeManager {
    public static void tpHome(ServerPlayerEntity player) {
        if (!Rusbik.players.containsKey(player.getName().getString())) {
            player.sendMessage(new LiteralText("Error al ejecutar /home, prueba a conectarte de nuevo o contacta con un administrador."), false);
            return;
        }
        HomePos homePos = Rusbik.players.get(player.getName().getString()).home.getHomePos();
        // Intentar hacer tp a tu última home.
        if (homePos.isValid()) {
            player.teleport(KrusbibUtils.getWorld(homePos.dim, player), homePos.X, homePos.Y, homePos.Z, player.yaw, player.pitch);
            player.addExperience(0); // xp gets reset when you tp from other dimension and needs to update smh, mojang pls.
        }
        else player.sendMessage(new LiteralText("Aun no has configurado tu casa, usa /setHome"), false);
    }
}
