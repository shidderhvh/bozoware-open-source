package bozoware.impl.module.world;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.block.EventAABB;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.property.EnumProperty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockHopper;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import static java.lang.Math.toRadians;

@ModuleData(moduleName = "Phase", moduleCategory = ModuleCategory.WORLD)
public class Phase extends Module {

    public static boolean phasing;

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<EventAABB> onEventAABB;
    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    private final EnumProperty<Mode> theMode = new EnumProperty<>("Mode", Mode.Aris, this);
    static boolean sneaking;
    private int moveUnder;

    public Phase(){
        moveUnder = 1;
        onModuleDisabled = () -> {

        };
        onPacketReceiveEvent = (e -> {
            switch (this.theMode.getPropertyValue()){
                case Aris:
                    if (e.getPacket() instanceof S08PacketPlayerPosLook && moveUnder == 2) {
                        moveUnder = 1;
                    }
                    break;
            }
        });
        onUpdatePositionEvent = (e -> {
            switch(theMode.getPropertyValue()) {
                case SetPos:
                    if (mc.thePlayer.onGround) {
                        if (mc.thePlayer.isSneaking() && mc.thePlayer.isCollidedHorizontally) {
                            double[] vals = {0.333, 0};
                            for (double i : vals) {
                                float yaw = mc.thePlayer.rotationYaw;
                                double dist = 0.25;
                                double x = mc.thePlayer.posX;
                                double y = mc.thePlayer.posY;
                                double z = mc.thePlayer.posZ;
                                mc.thePlayer.setPosition(x + (-Math.sin(toRadians(yaw)) * dist), mc.thePlayer.posY, z + (Math.cos(toRadians(yaw)) * dist));

                            }
                        }
                    }
                    break;
            }
    });
        onEventAABB = (e -> {
            switch (this.theMode.getPropertyValue()) {
                case Aris:
                    if (isInsideBlock()) {
                        e.setBoundingBox((AxisAlignedBB)null);
                    }
                    break;
            }
        });
    }
    private boolean isInsideBlock() {
        for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
            for (int y = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY); y < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxY) + 1; y++) {
                for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
                    Block block = mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if ((block != null) && (!(block instanceof BlockAir))) {
                        AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mc.theWorld, new BlockPos(x, y, z), mc.theWorld.getBlockState(new BlockPos(x, y, z)));
                        if ((block instanceof BlockHopper)) {
                            boundingBox = new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
                        }
                        if ((boundingBox != null) && (mc.thePlayer.getEntityBoundingBox().intersectsWith(boundingBox))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    public enum Mode {
        Aris,
        SetPos
    }

}
