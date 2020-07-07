package rusbik.home;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import rusbik.Rusbik;
import rusbik.RusbikFileManager;

public class HomeFileManager {

    public static void setHome(ServerPlayerEntity player, World world, double x, double y, double z){
        try{
            JSONArray playerList = RusbikFileManager.getFileContent();
            playerList.forEach(pl -> parsePlayerHome((JSONObject)pl, player, world, x, y, z));
            RusbikFileManager.updateFile();
            player.sendMessage(new LiteralText("Casa en: " + Rusbik.getDimensionWithColor(world) + Rusbik.formatCoords(x, y, z)), false);
        } catch (Exception ignored) { }
    }

    public static void parsePlayerHome(JSONObject playerObj, ServerPlayerEntity player, World world, double x, double y, double z){
        JSONObject playerObject = (JSONObject) playerObj.get("player");
        String name = (String)playerObject.get("name");
        if (name.equals(player.getName().getString())){
            RusbikFileManager.updatedPlayerList.add(updateHomePos(playerObject.get("death"), playerObject.get("name"), playerObject.get("perms"), x, y, z, Rusbik.getDim(world)));
        }
        else {
            RusbikFileManager.updatedPlayerList.add(playerObj);
        }
    }

    private static JSONObject updateHomePos(Object death, Object name, Object perms, double x, double y, double z, String dim){
        JSONObject playerInformation = new JSONObject();
        playerInformation.put("death", death);
        playerInformation.put("name", name);
        playerInformation.put("perms", perms);

        JSONObject playerHomePos = new JSONObject();
        playerHomePos.put("dim", dim);
        playerHomePos.put("x", String.valueOf(x));
        playerHomePos.put("y", String.valueOf(y));
        playerHomePos.put("z", String.valueOf(z));
        playerInformation.put("home", playerHomePos);

        JSONObject playersObject = new JSONObject();
        playersObject.put("player", playerInformation);

        return playersObject;
    }

    public static void tpHome(ServerPlayerEntity player){
        JSONArray playerList = RusbikFileManager.getFileContent();
        playerList.forEach(pl -> parsePlayerHome((JSONObject)pl, player));
    }

    public static void parsePlayerHome(JSONObject playerObj, ServerPlayerEntity player){
        JSONObject playerObject = (JSONObject) playerObj.get("player");
        String name = (String)playerObject.get("name");
        if (name.equals(player.getName().getString())){
            JSONObject homeObject = (JSONObject)playerObject.get("home");
            String x = (String) homeObject.get("x");
            String y = (String) homeObject.get("y");
            String z = (String) homeObject.get("z");
            String dim = (String) homeObject.get("dim");
            if (x.equals("") || y.equals("") || z.equals("") || dim.equals("")){
                player.sendMessage(new LiteralText("Necesitas configurar una casa antes, usa /setHome"), false);
            }

            else {
                ServerWorld dimension = player.getServer().getWorld(World.OVERWORLD);
                switch (dim){
                    case "Overworld":
                        dimension = player.getServer().getWorld(World.OVERWORLD);
                        break;
                    case "Nether":
                        dimension = player.getServer().getWorld(World.NETHER);
                        break;
                    case "End":
                        dimension = player.getServer().getWorld(World.END);
                        break;
                }
                player.teleport(dimension, Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z), player.yaw, player.pitch);
            }
        }
    }
}
