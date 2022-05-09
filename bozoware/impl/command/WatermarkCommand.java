package bozoware.impl.command;

import bozoware.base.BozoWare;
import bozoware.base.command.Command;

import java.util.Arrays;

public class WatermarkCommand implements Command {

    public static String watermark = "BozoWare";
    @Override
    public String[] getAliases() {
        return new String[]{"watermark", "clientname"};
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length >= 2) {
            watermark = arguments[1];
            for(int i = 2; i< Arrays.stream(arguments).count(); i++) {
                watermark += " " + arguments[i];
            }
            watermark = watermark.replaceAll("<build>", BozoWare.getInstance().CLIENT_VERSION);
            watermark = watermark.replaceAll("<user>", BozoWare.BozoUserName);
        }
    }

    @Override
    public String getUsage() {
        return ".watermark <watermark>";
    }
}