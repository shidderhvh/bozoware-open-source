
package bozoware.impl.module.combat;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.misc.TimerUtil;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.property.EnumProperty;
import bozoware.impl.property.ValueProperty;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleData(moduleName = "Criticals", moduleCategory = ModuleCategory.COMBAT)
public class Criticals extends Module {
    /*
    Copyright Pablo Matias 2022
    None of this code to be reused without my written permission
    Intellectual Rights owned by Pablo Matias
    */
    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    int ticksSinceSendCritical;
    double[] watchdogOffsets = {0.06f, 0.01f};

    public TimerUtil timer = new TimerUtil();
    Integer a;

    private final EnumProperty<critmodes> Mode = new EnumProperty<>("Mode", critmodes.Packet, this);

    public Criticals() {
        setModuleSuffix(Mode.getPropertyValue().toString());
        onModuleEnabled = () -> {
            timer.reset();
        };
        onPacketReceiveEvent = (PacketReceiveEvent -> {

        });
        onUpdatePositionEvent = (e -> {
            if(e.isPre)
                this.ticksSinceSendCritical++;
            switch (Mode.getPropertyValue()){
                case Packet:
                    if (mc.thePlayer.isSwingInProgress && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround && mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
                        mc.thePlayer.yC04(0.0625, false);
                        mc.thePlayer.yC04(0, false);
                    }
                    break;
                case NCP:
                    if (mc.thePlayer.isSwingInProgress && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
                        if(Aura.target == null ? mc.objectMouseOver.entityHit instanceof EntityLivingBase && mc.objectMouseOver.entityHit.hurtResistantTime != 20 : Aura.target.hurtResistantTime != 20)
                        e.setY(e.getY() + 0.003);
                        if (mc.thePlayer.ticksExisted % 10 == 0) {
                            e.setY(e.getY() + 0.001);
                        }
                        e.setOnGround(false);
                    }
                    break;


                case Verus:
                    if (mc.thePlayer.isSwingInProgress && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround && mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
                        mc.thePlayer.yC04(0.11, false);
                        mc.thePlayer.yC04(0.1100013579, false);
                        mc.thePlayer.yC04(0.0000013579, false);
                        mc.thePlayer.yC04(0.0000013579, false);
                    }
                    break;
            }
        });
        Mode.onValueChange = () -> setModuleSuffix(Mode.getPropertyValue().name());
    }

    private enum critmodes {
        Packet,
        NCP,
        Verus
    }
    public int getPing() {
        final NetworkPlayerInfo info = this.mc.getNetHandler().getPlayerInfo(this.mc.thePlayer.getUniqueID());
        return info == null ? 0 : (int) Math.ceil(info.getResponseTime() / 50.0);
    }
}
