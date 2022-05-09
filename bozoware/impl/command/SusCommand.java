package bozoware.impl.command;

import bozoware.base.command.Command;
import bozoware.base.command.CommandArgumentException;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SusCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"sus", "ph"};
    }

    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        try {
            Desktop.getDesktop().browse(new URI("https://pornhub.com/gay"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUsage() {
        return null;
    }
}
