package rusbik.helpers;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import rusbik.database.RusbikDatabase;
import rusbik.utils.KrusbibUtils;

import java.sql.SQLException;

public class BackManager {
    public static void tpDeathPos(ServerPlayerEntity player) throws SQLException {
        BackPos deathPos = RusbikDatabase.getDeathPos(player.getName().getString());
        // Intentar hacer tp a tu última deathPos.
        if (deathPosExist(deathPos.X)) {
            player.teleport(KrusbibUtils.getWorld(deathPos.dim, player), deathPos.X, deathPos.Y, deathPos.Z, player.yaw, player.pitch);
        }
        else{
            player.sendMessage(new LiteralText("Aun no has muerto :("), false);
        }
    }

    private static boolean deathPosExist (double X){
        return X != 0.0;
    }
}
