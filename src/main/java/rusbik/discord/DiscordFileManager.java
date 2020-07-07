package rusbik.discord;

import java.io.*;

public class DiscordFileManager {
    public static String name = "discord.conf";

    private static void createFile(){
        File file = new File(name);
        try{
            file.createNewFile();
        }
        catch (Exception e){
            System.out.println(e);
        }

    }

    public static void writeFile(String token, String channelId, Boolean running){
        File file = new File(name);
        if (!file.exists()){
            createFile();
        }
        try {
            FileWriter writer = new FileWriter(name);
            writer.write("discord " + token + " " + channelId + " " + running);
            writer.flush();
            writer.close();
        }
        catch (IOException e){
            System.out.println(e);
        }

    }

    public static String[] readFile(){
        String token = "";
        String channelId = "";
        String running = "";
        try{
            FileReader fr = new FileReader(name);
            BufferedReader br = new BufferedReader(fr);
            int i;
            StringBuilder result = new StringBuilder();
            while((i = br.read())!=-1){
                result.append((char) i);
            }
            if (result.toString().startsWith("discord")){
                token = result.toString().split(" ")[1];
                channelId = result.toString().split(" ")[2];
                running = result.toString().split(" ")[3];
            }
            br.close();
            fr.close();
        }
        catch (Exception e){
            System.out.println(e);
        }
        return new String[] {token, channelId, running};
    }

    public static void updateFile(Boolean running){
        String[] result = readFile();
        try{
            DiscordFileManager.writeFile(result[0], result[1], running);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
}
