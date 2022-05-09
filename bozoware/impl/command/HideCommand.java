package bozoware.impl.command;

import bozoware.base.BozoWare;
import bozoware.base.command.Command;
import bozoware.base.command.CommandArgumentException;
import bozoware.base.module.Module;
import java.util.ArrayList;

public class HideCommand implements Command {

    public static final ArrayList<Module> isHidden = new ArrayList<>();

    @Override
    public String[] getAliases() {
        return new String[]{"hide", "show"};
    }

    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        if (arguments.length == 2) {
            for (Module module : BozoWare.getInstance().getModuleManager().getModules()) {
                if (module.getModuleName().replaceAll(" ", "").equalsIgnoreCase(arguments[1])) {
                    if(!isHidden.contains(module))
                    isHidden.add(module);
                    else
                        isHidden.remove(module);
                    return;
                }
            }
        }
    }

    @Override
    public String getUsage() {
        return ".hide <module>";
    }
}
