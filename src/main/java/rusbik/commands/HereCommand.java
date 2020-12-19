package rusbik.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import rusbik.utils.KrusbibUtils;

import static net.minecraft.server.command.CommandManager.literal;

public class HereCommand {
    // Comando /here para sacar las coordenadas del jugador que lo ejecuta, saca la posición relativa en nether u overworld si fuera posible y aplica 5 segundos de glowing.
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher){
        dispatcher.register(literal("here").
                executes(context -> sendLocation(context.getSource())));
    }

    public static int sendLocation(ServerCommandSource source) throws CommandSyntaxException {
        if (source.getPlayer() != null) {
            ServerPlayerEntity player = source.getPlayer();
            String playerPos = KrusbibUtils.formatCoords(source.getPlayer().getPos().x, source.getPlayer().getPos().y, source.getPlayer().getPos().z);
            String playerNetherPos = KrusbibUtils.formatCoords(source.getPlayer().getPos().x / 8, source.getPlayer().getPos().y / 8, source.getPlayer().getPos().z / 8);
            String playerOverworldPos = KrusbibUtils.formatCoords(source.getPlayer().getPos().x * 8, source.getPlayer().getPos().y * 8, source.getPlayer().getPos().z * 8);
            String dimension = KrusbibUtils.getDimensionWithColor(player);

            if (player.world.getRegistryKey().getValue().equals(World.OVERWORLD.getValue())) source.getMinecraftServer().getPlayerManager().broadcastChatMessage(new LiteralText(Formatting.YELLOW + source.getPlayer().getName().asString() + " " + dimension + " " + playerPos + " -> " + Formatting.RED + "[Nether] " + playerNetherPos), MessageType.CHAT, Util.NIL_UUID);
            else if (player.world.getRegistryKey().getValue().equals(World.NETHER.getValue())) source.getMinecraftServer().getPlayerManager().broadcastChatMessage(new LiteralText(Formatting.YELLOW + source.getPlayer().getName().asString() + " " + dimension + " " + playerPos + " -> " + Formatting.GREEN + "[Overworld] " + playerOverworldPos), MessageType.CHAT, Util.NIL_UUID);
            else if (player.world.getRegistryKey().getValue().equals(World.END.getValue())) source.getMinecraftServer().getPlayerManager().broadcastChatMessage(new LiteralText(Formatting.YELLOW + source.getPlayer().getName().asString() + " " + dimension + " " + playerPos), MessageType.CHAT, Util.NIL_UUID);

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 100, 0, false, false));
        }
        return 1;
    }
}
