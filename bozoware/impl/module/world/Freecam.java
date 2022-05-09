package bozoware.impl.module.world;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.base.util.player.MovementUtil;
import bozoware.impl.event.block.EventAABB;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.network.PacketSendEvent;
import bozoware.impl.event.player.PlayerPushEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.property.ValueProperty;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleData(moduleName = "Freecam", moduleCategory = ModuleCategory.WORLD)
public class Freecam extends Module {

    private final ValueProperty<Double> Speed = new ValueProperty<>("Freecam Speed", 1D, 0.1D, 5D, this);

    @EventListener
    EventConsumer<PlayerPushEvent> onPlayerPushEvent;
    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;
    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;
    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<EventAABB> onEventAABB;
    public EntityOtherPlayerMP copy;

    public Freecam() {
        onModuleEnabled = () -> {
            Wrapper.getPlayer().motionY = 0;
            (this.copy = new EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.getGameProfile())).clonePlayer(mc.thePlayer, true);
            this.copy.setLocationAndAngles(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
            this.copy.rotationYawHead = mc.thePlayer.rotationYawHead;
            this.copy.setEntityId(-6969);
            this.copy.setSneaking(mc.thePlayer.isSneaking());
            mc.theWorld.addEntityToWorld(this.copy.getEntityId(), this.copy);
        };
        onModuleDisabled =() -> {
            MovementUtil.setSpeed(0);
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
        onUpdatePositionEvent = (e -> {
            mc.thePlayer.noClip = true;
            mc.thePlayer.setVelocity(0.0, 0.0, 0.0);
            Wrapper.getPlayer().motionY = 0;
                if (mc.thePlayer.movementInput.jump) {
                    Wrapper.getPlayer().motionY = Speed.getPropertyValue() / 2;
                }
                if (mc.thePlayer.movementInput.sneak) {
                    Wrapper.getPlayer().motionY = -Speed.getPropertyValue() / 2;
                }
                MovementUtil.setMoveSpeed(Speed.getPropertyValue());
        });
        onPacketReceiveEvent = (e -> {
            if (e.getPacket() instanceof C03PacketPlayer || e.getPacket() instanceof C02PacketUseEntity) {
                e.setCancelled(true);
            }
        });
        onEventAABB = (e -> {
            e.setBoundingBox(null);
        });
        onPlayerPushEvent = (e -> {
            e.setCancelled(true);
        });
    }
}
