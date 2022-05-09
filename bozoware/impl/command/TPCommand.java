package bozoware.impl.command;

import bozoware.base.BozoWare;
import bozoware.base.command.Command;
import bozoware.base.command.CommandArgumentException;
import bozoware.base.util.misc.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class TPCommand implements Command {

    public static String player;

    @Override
    public String[] getAliases() {
        return new String[]{"tp"};
    }


    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        if (arguments.length == 2) {
            player = arguments[1];
        }
    }

    @Override
    public String getUsage() {
        return ".tp <player>";
    }
}
