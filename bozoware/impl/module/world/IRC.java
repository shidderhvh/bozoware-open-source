package bozoware.impl.module.world;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.network.NetworkUtil;
import bozoware.impl.event.network.PacketSendEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import net.minecraft.network.play.client.C01PacketChatMessage;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@ModuleData(moduleName = "IRC", moduleCategory = ModuleCategory.WORLD)
public class IRC extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;


    public IRC(){
        onPacketSendEvent = (e -> {
            if(e.getPacket() instanceof C01PacketChatMessage){
                C01PacketChatMessage c01 = (C01PacketChatMessage) e.getPacket();
                if(c01.getMessage().startsWith("-")){
                    e.setCancelled(true);
                }
            }
        });
    }
    public void printToIRC() throws IOException {
        URL url = new URL("https://bozowareauth.000webhostapp.com/");
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
//        connection.addRequestProperty();
    }

}
