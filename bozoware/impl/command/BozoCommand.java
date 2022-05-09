package bozoware.impl.command;

import bozoware.base.BozoWare;
import bozoware.base.command.Command;
import bozoware.base.command.CommandArgumentException;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BozoCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"bozo", "BOZO"};
    }

    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        try {
            Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=GU4Hw-xbBNo&ab"));
            System.out.println("BOZO DOWN!!");
            BozoWare.getInstance().chat("BOZO DOWN");
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUsage() {
        return null;
    }
}
