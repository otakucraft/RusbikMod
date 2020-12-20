package rusbik.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import rusbik.Rusbik;
import rusbik.settings.RusbisConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    // Archivo de configuración.
    public static String yamlFile = "config.yaml";

    public static void initializeYaml() {
        File file = new File(yamlFile);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        if (!file.exists()) {
            try {
                // Se ejecuta al crearse el archivo a modo de helper.
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
                Rusbik.config = new RusbisConfig("", 0, false, 0, whitelistChat, allowedChat, adminChat, 0, validRoles);
                mapper.writeValue(file, Rusbik.config);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                Rusbik.config = mapper.readValue(file, RusbisConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Actualizar el archivo con los nuevos datos.
    public static void updateFile() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            mapper.writeValue(new File(yamlFile), Rusbik.config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
