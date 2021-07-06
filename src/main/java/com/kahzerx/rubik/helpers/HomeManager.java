package com.kahzerx.rubik.helpers;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import com.kahzerx.rubik.Rusbik;
import com.kahzerx.rubik.utils.KrusbibUtils;

public final class HomeManager {
    public static void tpHome(final ServerPlayerEntity player) {
        if (!Rusbik.players.containsKey(player.getName().getString())) {
            player.sendMessage(new LiteralText("Error al ejecutar /home, prueba a conectarte de nuevo o contacta con un administrador."), false);
            return;
        }
        HomePos homePos = Rusbik.players.get(player.getName().getString()).home.getHomePos();
        // Intentar hacer tp a tu última home.
        if (homePos.isValid()) {
            player.teleport(KrusbibUtils.getWorld(homePos.dim, player), homePos.x, homePos.y, homePos.z, player.getYaw(), player.getPitch());
            player.addExperience(0); // xp gets reset when you tp from other dimension and needs to update smh, mojang pls.
        } else {
            player.sendMessage(new LiteralText("Aun no has configurado tu casa, usa ").append(getSetHomeCommand()), false);
        }
    }

    private HomeManager() { }

    public static MutableText getSetHomeCommand() {
        return new LiteralText("/setHome").styled((style -> style.withColor(Formatting.DARK_GREEN).
                withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/setHome")).
                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("setHome")))));
    }
}
