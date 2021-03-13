package com.kahzerx.rubik.commands;

import com.kahzerx.rubik.database.RusbikDatabase;
import com.kahzerx.rubik.utils.KrusbibUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.Collections;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class BlockInfoCommand {
    /**
     * Comando para ver el historial de actualizaciones de un bloque.
     * @param dispatcher usado para registrar el comando.
     */
    public static void register(
            final CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("blockInfo").
                then(argument("coords", BlockPosArgumentType.blockPos()).
                        executes(context -> getInfo(context.getSource(),
                                BlockPosArgumentType.getBlockPos(
                                        context,
                                        "coords"),
                                1)).
                        then(argument("int", IntegerArgumentType.integer(1)).
                                executes(context -> getInfo(context.getSource(),
                                        BlockPosArgumentType.getBlockPos(
                                                context,
                                                "coords"),
                                        IntegerArgumentType.getInteger(
                                                context,
                                                "int"))))));
    }

    private BlockInfoCommand() { }

    public static int getInfo(
            final ServerCommandSource source,
            final BlockPos pos,
            final int page) {
        try {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            // Información almacenada en la base de datos,
            // extraigo los últimos 10 logs.
            List<String> msg = RusbikDatabase.getInfo(
                    x,
                    y,
                    z,
                    KrusbibUtils.getDim(source.getWorld()), page);
            // Les doy la vuelta para que sea más legible.
            Collections.reverse(msg);
            source.sendFeedback(
                    new LiteralText("======================"),
                    false);
            for (String line : msg) {
                source.sendFeedback(new LiteralText(line), false);
            }
            // Obtengo el número de páginas que tengo de esta posición.
            int nLine = RusbikDatabase.getLines(
                    x,
                    y,
                    z,
                    KrusbibUtils.getDim(source.getWorld()));

            if (page > nLine) {  // No hay páginas.
                return 1;
            } else if (page == nLine && page == 1) {  // Solo hay una página.
                source.sendFeedback(
                        new LiteralText(String.format(
                                "%d/%d.",
                                page,
                                nLine)),
                        false);
            } else if (page == 1) {
                // Estás en la posición 1 pero existen más páginas.
                MutableText pages = getPages(page, nLine);
                MutableText next = getNext(x, y, z, page);
                source.sendFeedback(
                        new LiteralText("").
                                append(pages).
                                append(next).
                                append(getHelp(
                                        x,
                                        y,
                                        z)),
                        false);
            } else if (page == nLine) {  // Estás en la última página de muchas.
                MutableText prev = getPrev(x, y, z, page);
                MutableText pages = getPages(page, nLine);
                source.sendFeedback(
                        new LiteralText("").
                                append(prev).
                                append(pages).
                                append(getHelp(
                                        x,
                                        y,
                                        z)),
                        false);
            } else {  // Tienes páginas antes y después.
                MutableText prev = getPrev(x, y, z, page);
                MutableText pages = getPages(page, nLine);
                MutableText next = getNext(x, y, z, page);
                source.sendFeedback(
                        new LiteralText("").
                                append(prev).
                                append(pages).
                                append(next).
                                append(getHelp(
                                        x,
                                        y,
                                        z)),
                        false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * Next page button.
     * @param x pos x.
     * @param y pos y.
     * @param z pos z.
     * @param page pag.
     * @return >>> clickable.
     */
    public static MutableText getNext(
            final int x,
            final int y,
            final int z,
            final int page) {
         return new LiteralText(" >>>").
                 styled((style ->
                         style.withColor(Formatting.GOLD).
                withClickEvent(
                        new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                String.format("/blockInfo %d %d %d %d",
                                        x,
                                        y,
                                        z,
                                        page + 1))).
                withHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new LiteralText(
                                        "Página siguiente")))));
    }

    /**
     * Botón clickable de página anterior.
     * @param x pos x.
     * @param y pos y.
     * @param z pos z.
     * @param page pag.
     * @return <<< clickable.
     */
    public static MutableText getPrev(
            final int x,
            final int y,
            final int z,
            final int page) {
        return new LiteralText("<<< ").
                styled((style ->
                        style.withColor(Formatting.GOLD).
                withClickEvent(
                        new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                String.format(
                                        "/blockInfo %d %d %d %d",
                                        x,
                                        y,
                                        z,
                                        page - 1))).
                withHoverEvent(
                        new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                new LiteralText(
                                        "Página anterior")))));
    }

    /**
     * Formatear la página en la que estás en el chat.
     * @param page página actual.
     * @param nLine total
     * @return página en la que estás.
     */
    public static MutableText getPages(
            final int page,
            final int nLine) {
        return new LiteralText(
                String.format(
                        "%d/%d",
                        page,
                        nLine)).
                styled((style -> style.withColor(Formatting.WHITE)));
    }

    /**
     * Mensaje de ayuda.
     * @param x posición x.
     * @param y posición y.
     * @param z posición z.
     * @return help msg.
     */
    public static MutableText getHelp(
            final int x,
            final int y,
            final int z) {  // Mensaje de ayuda.
        return new LiteralText(
                String.format(
                        ". También puedes especificar la página"
                                + " con /blockInfo %d %d %d <página>.",
                        x,
                        y,
                        z)).
                styled(style -> style.withColor(Formatting.WHITE));
    }
}
