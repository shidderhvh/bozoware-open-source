package bozoware.impl.command;

import bozoware.base.BozoWare;
import bozoware.base.command.Command;
import bozoware.base.command.CommandArgumentException;
import bozoware.base.module.Module;
import bozoware.base.util.Wrapper;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

public class BindCommand implements Command {

    @Override
    public String[] getAliases() {
        return new String[]{"bind", "b"};
    }

    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        if (arguments.length == 3) {
            for (Module module : BozoWare.getInstance().getModuleManager().getModules()) {
                if (module.getModuleName().replaceAll(" ", "").equalsIgnoreCase(arguments[1])) {
                    final String key = String.valueOf(Keyboard.getKeyIndex(arguments[2].toUpperCase()));
                    try {
                        final int parsedKey = Integer.parseInt(key);
                        module.setModuleBind(parsedKey);
                        Wrapper.getPlayer().addChatMessage(new ChatComponentText("Bound \247e" + module.getModuleName() + "\247f to key \247e" + arguments[2].toUpperCase()));
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                        return;
                    }
                    return;
                }
            }
        }
        throw new CommandArgumentException(getUsage());
    }

    @Override
    public String getUsage() {
        return ".bind <module> <key>";
    }
}
