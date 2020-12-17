package rusbik.helpers;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import rusbik.Rusbik;
import rusbik.database.RusbikDatabase;

import java.sql.SQLException;

public class BackManager {
    public static void tpDeathPos(ServerPlayerEntity player) throws SQLException {
        BackPos deathPos = RusbikDatabase.getDeathPos(player.getName().getString());
        if (deathPosExist(deathPos.X)){
            player.teleport(Rusbik.getWorld(deathPos.dim, player), deathPos.X, deathPos.Y, deathPos.Z, player.yaw, player.pitch);
        }
        else{
            player.sendMessage(new LiteralText("Aun no has muerto :("), false);
        }
    }

    private static boolean deathPosExist (double X){
        return X != 0.0;
    }
}
