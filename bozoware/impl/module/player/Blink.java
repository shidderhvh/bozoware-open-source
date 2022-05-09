package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.base.util.misc.TimerUtil;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.network.PacketSendEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.world.onWorldLoadEvent;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ModuleData(moduleName = "Blink", moduleCategory = ModuleCategory.PLAYER)
public class Blink extends Module {

    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;
    @EventListener
    EventConsumer<UpdatePositionEvent> updatePositionEvent;
    @EventListener
    EventConsumer<onWorldLoadEvent> onWorldLoadEvent;


    TimerUtil timer = new TimerUtil();

    public EntityOtherPlayerMP copy;
    private final Queue<Packet<?>> packetQueue = new ConcurrentLinkedQueue<>();

    public Blink() {
        onWorldLoadEvent = (e -> {
            if(!packetQueue.isEmpty())
            packetQueue.clear();
        });
        onModuleEnabled = () -> {
            packetQueue.clear();
                (this.copy = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile())).clonePlayer(mc.thePlayer, true);
            this.copy.setLocationAndAngles(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            this.copy.rotationYawHead = mc.thePlayer.rotationYawHead;
            this.copy.setEntityId(-6969);
            this.copy.setSneaking(mc.thePlayer.isSneaking());
//            mc.theWorld.addEntityToWorld(this.copy.getEntityId(), this.copy);
        };
        onModuleDisabled = () -> {
            if (this.copy != null && mc.theWorld != null && mc.thePlayer != null) {
                mc.thePlayer.setLocationAndAngles(this.copy.posX, this.copy.posY, this.copy.posZ, this.copy.rotationYaw, this.copy.rotationPitch);
                mc.thePlayer.rotationYawHead = this.copy.rotationYawHead;
                mc.theWorld.removeEntityFromWorld(this.copy.getEntityId());
                mc.thePlayer.setSneaking(this.copy.isSneaking());
                this.copy = null;
                mc.renderGlobal.loadRenderers();
            }
        };
        onPacketSendEvent = (e -> {
                if (e.getPacket() instanceof C03PacketPlayer || e.getPacket() instanceof C02PacketUseEntity) {
                    e.setCancelled(true);
                }
        });
        updatePositionEvent = (e -> {
        });
    }
}
