package bozoware.impl.command;

import bozoware.base.BozoWare;
import bozoware.base.command.Command;
import bozoware.base.command.CommandArgumentException;
import bozoware.base.util.Wrapper;

public class VClipCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"vclip", "clip"};
    }

    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        if (arguments.length == 2) {
            double parsedValue;
            try {
               parsedValue = Double.parseDouble(arguments[1]);
               Wrapper.getPlayer().setEntityBoundingBox(Wrapper.getPlayer().getEntityBoundingBox().offset(0, parsedValue, 0));
                BozoWare.getInstance().chat("VClipped " + parsedValue + " blocks!");
               return;
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
                throw new CommandArgumentException(getUsage());
            }
        }
        throw new CommandArgumentException(getUsage());
    }

    @Override
    public String getUsage() {
        return ".vclip <value>";
    }
}
