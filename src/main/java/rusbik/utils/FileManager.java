package rusbik.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rusbik.Rusbik;
import rusbik.settings.RubiConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    // Archivo de configuración.
    public static String directoryName = "";
    public static String jsonConfigFile = "RConfig.json";

    public static void initializeJson() {
        if (!directoryName.equals("")) jsonConfigFile = String.format("%s/%s", directoryName, jsonConfigFile);
        File file = new File(jsonConfigFile);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (!file.exists()) {
            try {
                List<Long> whitelistChat = new ArrayList<>();
                whitelistChat.add(0L);
                whitelistChat.add(1L);
                List<Long> allowedChat = new ArrayList<>();
                allowedChat.add(2L);
                allowedChat.add(3L);
                List<Long> adminChat = new ArrayList<>();
                adminChat.add(4L);
                adminChat.add(5L);
                List<Long> validRoles = new ArrayList<>();
                validRoles.add(6L);
                validRoles.add(7L);
                Rusbik.config = new RubiConfig("", 0, false, 0, whitelistChat, allowedChat, adminChat, 0, validRoles);
                FileWriter writer = new FileWriter(file);
                gson.toJson(Rusbik.config, writer);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                StringBuilder result = new StringBuilder();
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    result.append(line);
                }
                Rusbik.config = gson.fromJson(result.toString(), RubiConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Actualizar el archivo con los nuevos datos.
    public static void updateFile() {
        try {
            if (!directoryName.equals("")) jsonConfigFile = String.format("%s/%s", directoryName, jsonConfigFile);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileWriter writer = new FileWriter(jsonConfigFile);
            gson.toJson(Rusbik.config, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
