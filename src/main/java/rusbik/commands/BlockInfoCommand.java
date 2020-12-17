package rusbik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import rusbik.Rusbik;
import rusbik.database.RusbikDatabase;

import java.util.Collections;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BlockInfoCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("blockInfo").
                then(argument("coords", BlockPosArgumentType.blockPos()).
                        executes(context -> getInfo(context.getSource(), BlockPosArgumentType.getBlockPos(context, "coords"), 1)).
                        then(argument("int", IntegerArgumentType.integer(1)).
                                executes(context -> getInfo(context.getSource(), BlockPosArgumentType.getBlockPos(context, "coords"), IntegerArgumentType.getInteger(context, "int"))))));
    }

    public static int getInfo(ServerCommandSource source, BlockPos pos, int page){
        try{
            int X = pos.getX();
            int Y = pos.getY();
            int Z = pos.getZ();
            List<String> msg = RusbikDatabase.getInfo(X, Y, Z, Rusbik.getDim(source.getWorld()), page);
            Collections.reverse(msg);
            source.sendFeedback(new LiteralText("======================"), false);
            for (String line : msg){
                source.sendFeedback(new LiteralText(line), false);
            }
            int nLine = RusbikDatabase.getLines(X, Y, Z, Rusbik.getDim(source.getWorld()));
            if (page > nLine){
                return 1;
            }
            else if (page == nLine && page == 1){
                source.sendFeedback(new LiteralText(String.format("%d/%d.", page, nLine)), false);
            }
            else if (page == 1){
                MutableText pages = getPages(page, nLine);
                MutableText next = getNext(X, Y, Z, page);
                source.sendFeedback(new LiteralText("").append(pages).append(next).append(help(X, Y, Z)), false);
            }
            else if (page == nLine){
                MutableText prev = getPrev(X, Y, Z, page);
                MutableText pages = getPages(page, nLine);
                source.sendFeedback(new LiteralText("").append(prev).append(pages).append(help(X, Y, Z)), false);
            }
            else {
                MutableText prev = getPrev(X, Y, Z, page);
                MutableText pages = getPages(page, nLine);
                MutableText next = getNext(X, Y, Z, page);
                source.sendFeedback(new LiteralText("").append(prev).append(pages).append(next).append(help(X, Y, Z)), false);
            }
        }
        catch (Exception e){
            System.out.println(e);
        }
        return 1;
    }

    public static MutableText getNext(int X, int Y, int Z, int page) {
         return new LiteralText(" >>>").styled((style -> style.withColor(Formatting.GOLD).
                withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/blockInfo %d %d %d %d", X, Y, Z, page + 1))).
                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Página siguiente")))));
    }

    public static MutableText getPrev(int X, int Y, int Z, int page) {
        return new LiteralText("<<< ").styled((style -> style.withColor(Formatting.GOLD).
                withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/blockInfo %d %d %d %d", X, Y, Z, page - 1))).
                withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Página anterior")))));
    }

    public static MutableText getPages(int page, int nLine) {
        return new LiteralText(String.format("%d/%d", page, nLine)).styled((style -> style.withColor(Formatting.WHITE)));
    }

    public static MutableText help(int X, int Y, int Z) {
        return new LiteralText(String.format(". También puedes especificar la página con /blockInfo %d %d %d <página>.", X, Y, Z)).styled(style -> style.withColor(Formatting.WHITE));
    }
}
