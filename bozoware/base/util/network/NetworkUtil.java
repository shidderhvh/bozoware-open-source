package bozoware.base.util.network;

import bozoware.base.util.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;

public class NetworkUtil {
    static Minecraft mc = Minecraft.getMinecraft();
    public static int getPing() {
        NetworkPlayerInfo playerInfo = Wrapper.getMinecraft().getNetHandler().getPlayerInfo(Wrapper.getPlayer().getUniqueID());
        return playerInfo == null ? 0 : playerInfo.getResponseTime();
    }
    public static boolean isHypixel(){
        return (!mc.isSingleplayer() && mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net"));
    }
}
