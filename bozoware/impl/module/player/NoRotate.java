package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.network.PacketReceiveEvent;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

@ModuleData(moduleName = "No Rotate", moduleCategory = ModuleCategory.PLAYER)
public class NoRotate extends Module {

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    public NoRotate(){
        onPacketReceiveEvent = (e -> {
            if(e.getPacket() instanceof S08PacketPlayerPosLook){
                S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
                packet.setYaw(mc.thePlayer.rotationYaw);
                packet.setPitch(mc.thePlayer.rotationPitch);
            }
        });
    }


}
