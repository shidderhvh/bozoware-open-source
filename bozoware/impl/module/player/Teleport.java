package bozoware.impl.module.player;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.base.util.misc.TimerUtil;
import bozoware.impl.event.network.PacketSendEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C16PacketClientStatus;

@ModuleData(moduleName = "Teleport", moduleCategory = ModuleCategory.PLAYER)
public class Teleport extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePosEvent;
    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;
    boolean hasTeleported = false;
    TimerUtil timer = new TimerUtil();
    public Teleport(){
        onModuleEnabled = () -> {
            timer.reset();
            hasTeleported = false;
        };
        onUpdatePosEvent = (e -> {
            String dansName = "OGJediMaster";
            EntityPlayer danPlayer = null;
            for(EntityPlayer loadedPlayers : mc.theWorld.playerEntities){
                if(loadedPlayers.getName().equalsIgnoreCase(dansName))
                    danPlayer = loadedPlayers;
            }
            double x = 0, y = 0, z = 0;
            if(danPlayer != null && timer.hasReached(1050L) && !hasTeleported) {
                mc.thePlayer.setPosition(danPlayer.posX, danPlayer.posY, danPlayer.posZ);
                hasTeleported = true;
                this.toggleModule();
            }
            });
        onPacketSendEvent = (e -> {
            if(e.getPacket() instanceof C03PacketPlayer){
                e.setCancelled(true);
            }
        });
    }

}
