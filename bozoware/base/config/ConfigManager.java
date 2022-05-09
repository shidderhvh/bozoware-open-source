package bozoware.base.config;

import bozoware.base.BozoWare;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.Objects;

public class ConfigManager {

    public ConfigManager() {
        BozoWare.getInstance().getFileManager().addSubDirectory("configs");
    }

    public String getConfigDirectory() {
        return BozoWare.getInstance().getFileManager().getClientDirectory() + "/configs";
    }

    public boolean saveConfig(Config config) {
        String contentPrettyPrint = (new GsonBuilder()).setPrettyPrinting().create().toJson(config.saveJson());
        try {
            FileWriter fileWriter = new FileWriter(config.getFile());
            fileWriter.write(contentPrettyPrint);
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadConfig(String configName) {
        configName = configName + ".bozo";
        for (File configFile : Objects.requireNonNull(new File(getConfigDirectory()).listFiles())) {
            if (configFile.getName().equalsIgnoreCase(configName)) {
                try {
                    FileReader reader = new FileReader(configFile);
                    JsonParser jsonParser = new JsonParser();
                    JsonObject object = (JsonObject) jsonParser.parse(reader);
                    new Config(configFile.getName()).loadJson(object);
                    return true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }
}
