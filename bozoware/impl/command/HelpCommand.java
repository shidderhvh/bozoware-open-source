package bozoware.impl.command;

import bozoware.base.BozoWare;
import bozoware.base.command.Command;
import bozoware.base.command.CommandArgumentException;

public class HelpCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"help", "HELP"};
    }

    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        BozoWare.getInstance().chat("BozoWare");
        BozoWare.getInstance().chat("VClip Command - Adds Then Clips Your Y Postion");
        BozoWare.getInstance().chat("Sus/ph Command - Opens gay porn");
        BozoWare.getInstance().chat("Bozo Command - bozo");
        BozoWare.getInstance().chat("Bind Command - Binds Selected Module To Key");
        BozoWare.getInstance().chat("Config save/load/folder - Allows You To Save, Load And Open Your Configs Folder");
        BozoWare.getInstance().chat("Toggle/T Command - Toggles Module");
        BozoWare.getInstance().chat("Name Command - Copies Your Name To The Clipboard");
//        BozoWare.getInstance().chat("Ban Command - instantly bans you from hypixel (20 c13s");
    }

    @Override
    public String getUsage() {
        return null;
    }
}
