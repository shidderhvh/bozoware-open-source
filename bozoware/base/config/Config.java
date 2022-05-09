package bozoware.base.config;

import bozoware.base.BozoWare;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;

public class Config {

    private final File file;

    public Config(String configName) {
        file = new File(BozoWare.getInstance().getConfigManager().getConfigDirectory() + "/" + configName + ".bozo");
        if (!file.exists()) {
            try {
                if (file.createNewFile())
                    System.out.println("Config Created!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public File getFile() {
        return file;
    }

    public JsonObject saveJson() {
        JsonObject object = new JsonObject();
        JsonObject modules = new JsonObject();
        BozoWare.getInstance().getModuleManager().getModules().forEach(module -> {
            modules.add(module.getModuleName(), module.saveJson());
        });
        object.add("modules", modules);
        return object;
    }

    public void loadJson(JsonObject jsonObject) {
        if (jsonObject.has("modules")) {
            JsonObject modulesJson = jsonObject.getAsJsonObject("modules");
            BozoWare.getInstance().getModuleManager().getModules().forEach(module -> {
                if (modulesJson.has(module.getModuleName()))
                    module.loadJson(modulesJson.getAsJsonObject(module.getModuleName()));
            });
        }
    }

}
