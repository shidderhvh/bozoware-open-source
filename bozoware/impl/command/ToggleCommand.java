package bozoware.impl.command;

import bozoware.base.BozoWare;
import bozoware.base.command.Command;
import bozoware.base.command.CommandArgumentException;
import bozoware.base.module.Module;
import bozoware.base.util.Wrapper;
import net.minecraft.util.ChatComponentText;

public class ToggleCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"toggle", "t"};
    }

    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        if (arguments.length == 2) {
            for (Module module : BozoWare.getInstance().getModuleManager().getModules()) {
                if (module.getModuleName().replaceAll(" ", "").equalsIgnoreCase(arguments[1])) {
                    BozoWare.getInstance().getModuleManager().getModuleByName.apply(module.getModuleName()).toggleModule();
                    Wrapper.getPlayer().addChatMessage(new ChatComponentText(module.getModuleName() + " was " + (module.isModuleToggled() ? "\247aEnabled" : "\2474Disabled")));
                    return;
                }
            }
        }
        throw new CommandArgumentException(getUsage());
    }

    @Override
    public String getUsage() {
        return ".toggle <module>";
    }
}
