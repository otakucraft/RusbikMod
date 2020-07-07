package rusbik.perms;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import rusbik.Rusbik;
import rusbik.RusbikFileManager;

public class PermsFileManager {
    private static boolean playerFound = false;

    public static void setPerm(ServerPlayerEntity serverPlayerEntity, String player, int val){
        playerFound = false;
        JSONArray playerList = RusbikFileManager.getFileContent();
        playerList.forEach(pl -> parsePlayerPerms((JSONObject)pl, player, val));
        if (playerFound){
            RusbikFileManager.updateFile();
            serverPlayerEntity.sendMessage(new LiteralText(player + " perms = " + val), false);
            if (Rusbik.permsArray.containsKey(player)){
                Rusbik.permsArray.remove(player);
                Rusbik.permsArray.put(player, String.valueOf(val));
            }
        }
        else {
            serverPlayerEntity.sendMessage(new LiteralText("Unable to set perms"), false);
        }
        playerFound = false;
    }

    public static String setPerm(String player, int val){
        playerFound = false;
        JSONArray playerList = RusbikFileManager.getFileContent();
        playerList.forEach(pl -> parsePlayerPerms((JSONObject)pl, player, val));
        if (playerFound){
            RusbikFileManager.updateFile();
            if (Rusbik.permsArray.containsKey(player)){
                Rusbik.permsArray.remove(player);
                Rusbik.permsArray.put(player, String.valueOf(val));
            }
            playerFound = false;
            return player + " perms = " + val;
        }
        else {
            return "Unable to set perms";
        }
    }

    public static void parsePlayerPerms(JSONObject playerObj, String player, int val){
        JSONObject playerObject = (JSONObject) playerObj.get("player");
        String name = (String)playerObject.get("name");
        if (name.equals(player)){
            RusbikFileManager.updatedPlayerList.add(updatePerms(playerObject.get("death"), playerObject.get("name"), playerObject.get("home"), val));
        }
        else {
            RusbikFileManager.updatedPlayerList.add(playerObj);
        }
    }

    private static JSONObject updatePerms(Object death, Object name, Object home, int value){
        JSONObject playerInformation = new JSONObject();
        playerInformation.put("death", death);
        playerInformation.put("name", name);
        playerInformation.put("home", home);

        playerInformation.put("perms", String.valueOf(value));

        JSONObject playersObject = new JSONObject();
        playersObject.put("player", playerInformation);

        playerFound = true;

        return playersObject;
    }
}
