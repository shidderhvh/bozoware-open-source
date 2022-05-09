package bozoware.impl.command;

import bozoware.base.BozoWare;
import bozoware.base.command.Command;
import bozoware.base.command.CommandArgumentException;
import bozoware.base.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ConfigCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"config", "profile", "preset", "c"};
    }

    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        if(arguments.length == 1){
            throw new CommandArgumentException(getUsage());
        }
        if (arguments.length == 3 || arguments[1].equalsIgnoreCase("folder") || arguments[1].equalsIgnoreCase("list")) {
            final String action = arguments[1];
            switch (action.toLowerCase()) {
                case "load":
                    if (BozoWare.getInstance().getConfigManager().loadConfig(arguments[2])) {
                        BozoWare.getInstance().chat("Successfully loaded config \247e" + arguments[2]);
                        return;
                    } else
                        throw new CommandArgumentException(getUsage());
                case "save":
                    if (BozoWare.getInstance().getConfigManager().saveConfig(new Config(arguments[2]))) {
                        BozoWare.getInstance().chat("Successfully saved config \247e" + arguments[2]);
                        return;
                    } else
                        throw new CommandArgumentException(getUsage());
                case "folder":
                    try {
                        Desktop.getDesktop().open(new File(BozoWare.getInstance().getConfigManager().getConfigDirectory()));
                    } catch (IOException e) {
                        BozoWare.getInstance().chat("Couldn't open config folder");
                        e.printStackTrace();
                    }
                    return;
                case "list":
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("CONFIG LIST"));
                    for (File configFile : Objects.requireNonNull(new File((BozoWare.getInstance().getConfigManager().getConfigDirectory())).listFiles())) {
                        if (!configFile.getName().endsWith(".bozo.bozo"))
                            if (configFile.getName().endsWith(".bozo"))
                                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\2477" + configFile.getName().replaceAll(".bozo", "")));
                    }
            }
        }
        throw new CommandArgumentException(getUsage());
    }

    @Override
    public String getUsage() {
        return "config <load/save/folder/list> <config-name>";
    }
}
