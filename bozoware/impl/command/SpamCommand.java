package bozoware.impl.command;

import bozoware.base.BozoWare;
import bozoware.base.command.Command;

import java.util.Arrays;

public class SpamCommand implements Command {

    public static String spamMessage = "BOZOWARE > YOU NN!";
    @Override
    public String[] getAliases() {
        return new String[]{"spammessage", "spam"};
    }

    @Override
    public void execute(String[] arguments) {
        if (arguments.length >= 2) {
            spamMessage = arguments[1];
            for(int i = 2; i< Arrays.stream(arguments).count(); i++) {
                spamMessage += " " + arguments[i];
            }

            BozoWare.getInstance().chat("Spam message was set to " + spamMessage);
//            BozoWare.getInstance().chat(watermark);
        }
    }

    @Override
    public String getUsage() {
        return ".watermark <watermark>";
    }
}