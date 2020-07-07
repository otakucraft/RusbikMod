package rusbik;

import net.minecraft.server.network.ServerPlayerEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RusbikFileManager {
    private static final String fileName = "otakuCraftPlayers.json";

    private static boolean playerMatch = false;

    public static JSONArray updatedPlayerList = new JSONArray();

    public static JSONArray getFileContent(){
        tryCreatePlayerFile();
        JSONArray playerList = null;
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(fileName)) {
            Object obj = jsonParser.parse(reader);
            playerList = (JSONArray) obj;
        }
        catch (IOException | ParseException ignored) { }
        return playerList;
    }

    public static void tryCreatePlayerFile(){
        File file = new File(fileName);
        if (!file.exists()){
            try (FileWriter jsonFile = new FileWriter(fileName)){
                jsonFile.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println("unable to create players file");
            }
        }
    }

    private static void registerPlayer(ServerPlayerEntity player){
        JSONObject playerInformation = new JSONObject();

        JSONObject playerDeathPos = new JSONObject();
        playerDeathPos.put("dim", "");
        playerDeathPos.put("x", "");
        playerDeathPos.put("y", "");
        playerDeathPos.put("z", "");
        playerInformation.put("death", playerDeathPos);

        JSONObject playerHomePos = new JSONObject();
        playerHomePos.put("dim", "");
        playerHomePos.put("x", "");
        playerHomePos.put("y", "");
        playerHomePos.put("z", "");
        playerInformation.put("home", playerHomePos);

        playerInformation.put("name", player.getName().getString());

        playerInformation.put("perms", "1");

        JSONObject playersObject = new JSONObject();
        playersObject.put("player", playerInformation);

        JSONArray playerList = new JSONArray();
        playerList.add(playersObject);
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(playerList.toJSONString());
            file.flush();
        }
        catch (IOException ignored) { }
    }

    private static void registerPlayer(ServerPlayerEntity player, JSONArray playerList){
        JSONObject playerInformation = new JSONObject();

        JSONObject playerDeathPos = new JSONObject();
        playerDeathPos.put("dim", "");
        playerDeathPos.put("x", "");
        playerDeathPos.put("y", "");
        playerDeathPos.put("z", "");
        playerInformation.put("death", playerDeathPos);

        JSONObject playerHomePos = new JSONObject();
        playerHomePos.put("dim", "");
        playerHomePos.put("x", "");
        playerHomePos.put("y", "");
        playerHomePos.put("z", "");
        playerInformation.put("home", playerHomePos);

        playerInformation.put("name", player.getName().getString());

        playerInformation.put("perms", "1");

        JSONObject playersObject = new JSONObject();
        playersObject.put("player", playerInformation);

        playerList.add(playersObject);
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(playerList.toJSONString());
            file.flush();
        }
        catch (IOException ignored) { }
    }

    private static boolean checkIfPlayerExists(String player, JSONArray playerList){
        playerList.forEach(pl -> parsePlayersObject((JSONObject)pl, player));
        if (playerMatch){
            playerMatch = false;//tf is this
            return true;
        }
        return false;
    }

    private static void parsePlayersObject(JSONObject JsonPlayer, String player) {
        JSONObject playerObject = (JSONObject) JsonPlayer.get("player");
        String name = (String)playerObject.get("name");
        if (name.equals(player)){
            playerMatch = true;
        }
    }

    public static void updateFile(){
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(updatedPlayerList.toJSONString());
            file.flush();
            updatedPlayerList.clear();
        }
        catch (IOException ignored) { }
    }

    public static void onPlayerJoins(ServerPlayerEntity player){
        JSONArray playerList = getFileContent();
        if (playerList == null){
            registerPlayer(player);
        }
        else {
            boolean exists = checkIfPlayerExists(player.getDisplayName().getString(), playerList);
            if (!exists){
                registerPlayer(player, playerList);
            }
        }
    }
}
