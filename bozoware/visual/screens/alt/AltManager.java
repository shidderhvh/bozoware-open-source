
package bozoware.visual.screens.alt;

import bozoware.base.BozoWare;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


public class AltManager {
    public static Alt lastAlt;
    public static ArrayList<Alt> registry;
    private static File altsFile = new File(BozoWare.getInstance().getFileManager().getClientDirectory() + "/alts/alts.json");

    static {
        registry = new ArrayList<>();
        BozoWare.getInstance().getFileManager().addSubDirectory("alts");
        if (!altsFile.exists()) {
            try {
                altsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public ArrayList<Alt> getRegistry() {
        return registry;
    }

    public void setLastAlt(Alt alt2) {
        lastAlt = alt2;
    }

    public void onExit() {
        JsonObject saveJson = new JsonObject();
        for (Alt alt : this.getRegistry()) {
            JsonObject altJson = new JsonObject();
            altJson.addProperty("email", alt.getUsername());
            altJson.addProperty("password", alt.getPassword());
            saveJson.add(alt.getUsername(), altJson);
        }
        String contentPrettyPrint = (new GsonBuilder()).setPrettyPrinting().create().toJson(saveJson);
        try {
            FileWriter fileWriter = new FileWriter(altsFile);
            fileWriter.write(contentPrettyPrint);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onStart() {
        if (altsFile.length() > 0) {
            try {
                FileReader reader = new FileReader(altsFile);
                JsonParser jsonParser = new JsonParser();
                JsonObject object = (JsonObject) jsonParser.parse(reader);

                Set<Map.Entry<String, JsonElement>> entrySet = object.entrySet();
                for (Map.Entry<String, JsonElement> entry : entrySet) {
                    if (object.has(entry.getKey())) {
                        if (object.getAsJsonObject(entry.getKey()).has("email") &&
                                object.getAsJsonObject(entry.getKey()).has("password"))
                            this.getRegistry().add(new Alt(object.getAsJsonObject(entry.getKey()).get("email").getAsString(),
                                    object.getAsJsonObject(entry.getKey()).get("password").getAsString()));
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

