package bozoware.impl.module.visual;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.player.RenderNametagEvent;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.ChatComponentText;

@ModuleData(moduleName = "Name Hider", moduleCategory = ModuleCategory.VISUAL)
public class NameHider extends Module {
    @EventListener
    EventConsumer<RenderNametagEvent> onRenderNametagEvent;
    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    public NameHider(){
        onRenderNametagEvent = (e -> {
            e.setRenderedName("BOZO");
        });
        onPacketReceiveEvent = (ep -> {
            if(ep.getPacket() instanceof S45PacketTitle) {
                S45PacketTitle s45 = (S45PacketTitle) ep.getPacket();
                if(s45.getMessage().getUnformattedText().contains(mc.thePlayer.getName())){
                    s45.getMessage().getUnformattedText().replaceAll(mc.thePlayer.getName(), BozoWare.BozoUserName);
                }
            }
        });
    }
}
