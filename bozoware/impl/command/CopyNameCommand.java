package bozoware.impl.command;

import bozoware.base.BozoWare;
import bozoware.base.command.Command;
import bozoware.base.command.CommandArgumentException;
import bozoware.base.util.Wrapper;

import java.awt.*;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;

public class CopyNameCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"name", "copys name"};
    }

    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        if (arguments.length == 1) {
            try {
                StringSelection stringselection = new StringSelection(Wrapper.getPlayer().getName());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringselection, (ClipboardOwner)null);
                BozoWare.getInstance().chat("Copied Name!");
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
        return ".name";
    }
}
