package rusbik;

import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import rusbik.back.BackCommand;
import rusbik.home.HomeCommand;
import rusbik.home.SetHomeCommand;
import rusbik.perms.PermsCommand;
import rusbik.randomTp.RandomTpCommand;
import rusbik.teleport.AdminTeleportCommand;
import rusbik.teleport.CustomTeleportCommand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class Rusbik {

    public static HashMap<String, String> permsArray = new HashMap<>();


    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher){
        RandomTpCommand.register(dispatcher);
        SetHomeCommand.register(dispatcher);
        HomeCommand.register(dispatcher);
        PermsCommand.register(dispatcher);
        CustomTeleportCommand.register(dispatcher);
        AdminTeleportCommand.register(dispatcher);
        BackCommand.register(dispatcher);
    }

    public static String getDimensionWithColor(ServerPlayerEntity player) {
        Identifier dimensionType = player.world.getDimensionRegistryKey().getValue();
        String msg = player.world.getDimension().toString();
        if (dimensionType.equals(World.OVERWORLD.getValue())) msg = Formatting.GREEN + "[Overworld]";
        else if (dimensionType.equals(World.NETHER.getValue())) msg = Formatting.RED + "[Nether]";
        else if (dimensionType.equals(World.END.getValue())) msg = Formatting.DARK_PURPLE + "[End]";
        return msg;
    }

    public static String getDimensionWithColor(World world) {
        Identifier dimensionType = world.getDimensionRegistryKey().getValue();
        String msg = world.getDimension().toString();
        if (dimensionType.equals(World.OVERWORLD.getValue())) msg = Formatting.GREEN + "[Overworld]";
        else if (dimensionType.equals(World.NETHER.getValue())) msg = Formatting.RED + "[Nether]";
        else if (dimensionType.equals(World.END.getValue())) msg = Formatting.DARK_PURPLE + "[End]";
        return msg;
    }

    public static String getDim(World world){
        Identifier dimensionType = world.getDimensionRegistryKey().getValue();
        String msg = world.getDimension().toString();
        if (dimensionType.equals(World.OVERWORLD.getValue())) msg = "Overworld";
        else if (dimensionType.equals(World.NETHER.getValue())) msg = "Nether";
        else if (dimensionType.equals(World.END.getValue())) msg = "End";
        return msg;
    }

    public static String formatCoords(double x, double y, double z){
        return Formatting.WHITE + " [x: " + (int) x + ", y: " + (int) y + ", z: " + (int) z + "]";
    }

    public static Collection<String> getPlayers(ServerCommandSource source) {
        Set<String> players = Sets.newLinkedHashSet();
        players.addAll(source.getPlayerNames());
        return players;
    }
}
