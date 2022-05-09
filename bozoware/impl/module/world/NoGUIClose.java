package bozoware.impl.module.world;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.network.PacketReceiveEvent;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S2EPacketCloseWindow;

@ModuleData(moduleName = "No GUI Close", moduleCategory = ModuleCategory.WORLD)
public class NoGUIClose extends Module {
    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketRecieveEvent;

    public NoGUIClose(){
        onPacketRecieveEvent = (e -> {
            if(e.getPacket() instanceof S2EPacketCloseWindow)
                e.setCancelled(true);
        });
    }
}
