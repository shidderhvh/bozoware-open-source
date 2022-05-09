package bozoware.impl.command;

import bozoware.base.BozoWare;
import bozoware.base.command.Command;
import bozoware.base.command.CommandArgumentException;
import bozoware.base.util.Wrapper;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;

public class BanCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[]{"ban", "instaban", "sendc13"};
    }

    @Override
    public void execute(String[] arguments) throws CommandArgumentException {
        for(int i = 0; i<200; i++) {
//            PlayerCapabilities playerCapabilities = new PlayerCapabilities();
//            playerCapabilities.isFlying = true;
//            playerCapabilities.allowFlying = true;
            Wrapper.sendPacketDirect(new C0CPacketInput());
            BozoWare.getInstance().chat("Sent c13");
        }
    }

    @Override
    public String getUsage() {
        return null;
    }
}
