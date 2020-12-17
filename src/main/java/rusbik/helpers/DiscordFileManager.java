package rusbik.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import rusbik.Rusbik;
import rusbik.settings.RusbisConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DiscordFileManager {
    public static String yamlFile = "config.yaml";

    public static void initializeYaml() {
        File file = new File(yamlFile);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
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
                Rusbik.config = new RusbisConfig("", 0, false, whitelistChat, allowedChat, adminChat);
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

    public static void updateFile() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            mapper.writeValue(new File(yamlFile), Rusbik.config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
