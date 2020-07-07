package rusbik.perms;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import rusbik.Rusbik;
import rusbik.RusbikFileManager;

public class Perms {
    public static void addToArray(String player){
        JSONArray playerList = RusbikFileManager.getFileContent();
        playerList.forEach(pl -> parsePlayerPermsJoin((JSONObject)pl, player));
    }

    private static void parsePlayerPermsJoin(JSONObject playerObj, String player){
        JSONObject playerObject = (JSONObject) playerObj.get("player");
        String name = (String)playerObject.get("name");
        if (name.equals(player)){
            String value = (String)playerObject.get("perms");
            Rusbik.permsArray.put(name, value);
        }
    }

    public static void removeFromArray(String player){
        JSONArray playerList = RusbikFileManager.getFileContent();
        playerList.forEach(pl -> parsePlayerPermsLeft((JSONObject)pl, player));
    }

    private static void parsePlayerPermsLeft(JSONObject playerObj, String player){
        JSONObject playerObject = (JSONObject) playerObj.get("player");
        String name = (String)playerObject.get("name");
        if (name.equals(player)){
            Rusbik.permsArray.remove(name);
        }
    }
}
