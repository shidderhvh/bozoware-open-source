package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.player.MouseClickEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.property.ValueProperty;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

@ModuleData(moduleName = "Click TP", moduleCategory = ModuleCategory.PLAYER)
public class ClickTP extends Module {
    private boolean dispatchTeleport;

    @EventListener
    EventConsumer<MouseClickEvent> onMouseClickEvent;
    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final ValueProperty<Double> blocks = new ValueProperty<>("Reach", 25D, 5D, 500D, this);

    public ClickTP(){
        onUpdatePositionEvent = (e ->{
            if (e.isPre()) {
                MovingObjectPosition ray = rayTrace(blocks.getPropertyValue());
                if (ray == null) {
                    return;
                }
                if (dispatchTeleport) {
                    e.setOnGround(true);
                    double x_new = ray.getBlockPos().getX() + 0.5D;
                    double y_new = ray.getBlockPos().getY() + 1;
                    double z_new = ray.getBlockPos().getZ() + 0.5D;
                    double distance = mc.thePlayer.getDistance(x_new, y_new, z_new);
                    for (double d = 0.0D; d < distance; d += 2.0D) {
                        setPos(mc.thePlayer.posX + (x_new - mc.thePlayer.getHorizontalFacing().getFrontOffsetX() - mc.thePlayer.posX) * d / distance, mc.thePlayer.posY + (y_new - mc.thePlayer.posY) * d / distance, mc.thePlayer.posZ + (z_new - mc.thePlayer.getHorizontalFacing().getFrontOffsetZ() - mc.thePlayer.posZ) * d / distance);
                    }
                    setPos(x_new, y_new, z_new);
                    mc.renderGlobal.loadRenderers();
                    dispatchTeleport = false;
                }
            }
        });
    }
    public MovingObjectPosition rayTrace(double blockReachDistance) {
        Vec3 vec3 = mc.thePlayer.getPositionEyes(1.0F);
        Vec3 vec4 = mc.thePlayer.getLookVec();
        Vec3 vec5 = vec3.addVector(vec4.xCoord * blockReachDistance, vec4.yCoord * blockReachDistance, vec4.zCoord * blockReachDistance);
        return mc.theWorld.rayTraceBlocks(vec3, vec5, !mc.thePlayer.isInWater(), false, false);
    }

    public void setPos(double x, double y, double z) {
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
        mc.thePlayer.setPosition(x, y, z);
    }

}
