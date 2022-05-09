package bozoware.impl.module.player;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.impl.event.block.EventAABB;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.module.movement.Flight;
import bozoware.impl.property.EnumProperty;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.apache.commons.lang3.RandomUtils;

@ModuleData(moduleName = "No Fall", moduleCategory = ModuleCategory.PLAYER)
public class NoFall extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<EventAABB> onEventAABB;

    private final EnumProperty<NoFallModes> noFallModesProperty = new EnumProperty<>("Mode", NoFallModes.Edit, this);

    private float lastTickFallDist, fallDist;

    public NoFall() {
        onUpdatePositionEvent = (updatePositionEvent -> {
            fallDist += mc.thePlayer.fallDistance - lastTickFallDist;
            lastTickFallDist = mc.thePlayer.fallDistance;
            if (updatePositionEvent.isPre()) {
                setModuleSuffix(noFallModesProperty.getPropertyValue().name());
                switch (noFallModesProperty.getPropertyValue()){
                    case TP:
                        if (mc.thePlayer.fallDistance >= 3 && isBlockUnder()) {
                            final BlockPos blockpos = getClosestBlockUnder();
                            mc.thePlayer.fallDistance = 0;
                            mc.thePlayer.getEntityBoundingBox().offsetAndUpdate(0, -(mc.thePlayer.posY - blockpos.getY() - 1), 0);
                        }
                        break;
                    case Edit:
                        if (Wrapper.getPlayer().fallDistance > 3) {
                            updatePositionEvent.setOnGround(true);
                        }
                        break;
                    case Watchdog:
                        if (mc.thePlayer.fallDistance > 2.75 && updatePositionEvent.isPre()) {
                            Wrapper.sendPacketDirect(new C03PacketPlayer(true));
                            mc.thePlayer.fallDistance = 0.0f;
                        }
                            break;
                    case Ground:
                        if (fallDist > 2) {
                            updatePositionEvent.setOnGround(true);
                            fallDist = 0;
                        }
                        break;
                    case NoGround:
                        if (mc.thePlayer.onGround)
                            updatePositionEvent.setY(updatePositionEvent.getY() + RandomUtils.nextDouble(0.0001, 0.001));
                        updatePositionEvent.setOnGround(false);
                        break;
                }
            }
        });
        onEventAABB = (e -> {
            switch(noFallModesProperty.getPropertyValue()){
                case Verus:
                    if(BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Flight.class).isModuleToggled())
                        return;
                    if(mc.thePlayer.fallDistance >= 2 && e.getPos().getY() < mc.thePlayer.posY && !mc.gameSettings.keyBindSneak.pressed) {
                        AxisAlignedBB blockBB = AxisAlignedBB.fromBounds(-5000, -1, -5000, 5000, 1, 5000).offset(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ());
                        e.setBoundingBox(blockBB);
                    }
                    break;
            }
        });
    }

    private enum NoFallModes {
        Edit,
        Ground,
        NoGround,
        Watchdog,
        TP,
        Verus
    }
    private boolean isBlockUnder() {
        for (int i = (int) (mc.thePlayer.posY - 1.0); i > 0; --i) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);
            if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) continue;
            return true;
        }
        return false;
    }

    private BlockPos getClosestBlockUnder() {
        for (int i = (int) mc.thePlayer.posY - 1; i > 0; --i) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ);
            if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockAir) continue;
            if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                return new BlockPos(pos.getX(),pos.getY() + 1,pos.getZ());
            } else {
                return pos;
            }
        }
        return mc.thePlayer.getPosition();
    }
}
