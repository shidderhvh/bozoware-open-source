package bozoware.impl.module.movement;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.player.PlayerStepEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.module.player.BlockFly;
import bozoware.impl.property.EnumProperty;
import bozoware.impl.property.ValueProperty;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@ModuleData(moduleName = "Step", moduleCategory = ModuleCategory.MOVEMENT)
public class Step extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;
    @EventListener
    EventConsumer<PlayerStepEvent> onStepEvent;

    private final EnumProperty<Modes> Mode = new EnumProperty<>("Mode", Modes.Vanilla, this);
    private final ValueProperty<Float> Height = new ValueProperty<Float>("Step Height", 1F, 1F, 3F, this);

    private double stepTimer;
    private boolean lessPackets;
    private int cancelledPackets;

    double preY;
    private final double[][] offsets = {
            {0.41999998688698d, 0.7531999805212d}
    };
    public Step() {
        setModuleSuffix(Mode.getPropertyValue().toString());
        onModuleDisabled = () -> {
            mc.timer.timerSpeed = 1;
            mc.thePlayer.stepHeight = 0.5F;
        };
        onModuleEnabled = () -> {

        };
        onStepEvent = (e -> {
            switch (Mode.getPropertyValue()) {
                case NCP:
                    final double height = this.mc.thePlayer.getEntityBoundingBox().minY - this.mc.thePlayer.posY;
                    final boolean canStep = height >= 0.625 && mc.thePlayer.onGround && mc.thePlayer.isCollidedVertically && !BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Speed.class).isModuleToggled() && !BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Flight.class).isModuleToggled() && !BozoWare.getInstance().getModuleManager().getModuleByClass.apply(BlockFly.class).isModuleToggled();
                    if (!this.mc.thePlayer.isInWater() && !this.mc.thePlayer.isInLava() &&
                            this.mc.thePlayer.onGround && this.mc.thePlayer.isCollidedVertically &&
                            !this.mc.thePlayer.isOnLadder()) {
                        mc.thePlayer.stepHeight = Height.getPropertyValue();
                        if (mc.timer.timerSpeed == 0.25 && e.isPre())
                            mc.timer.timerSpeed = 1;
                        if (!this.mc.thePlayer.isInWater() && !this.mc.thePlayer.isInLava() &&
                                !this.mc.thePlayer.isOnLadder() && canStep) {
                            if (e.isPre()) {
                                e.setStepHeight(Height.getPropertyValue());
                            } else {
                                if (e.getHeightStepped() > this.mc.thePlayer.stepHeight) {
                                    for (double[] offset : offsets) {
                                        mc.thePlayer.yC04(offset[0], false);
                                        mc.thePlayer.yC04(offset[1], false);
                                    }
                                    this.stepTimer = 1.0 / (offsets.length + 1);
                                    this.lessPackets = true;
                                }
                            }
                        }
                    }
            }
        });
        onPacketReceiveEvent = (xd -> {

        });
        onUpdatePositionEvent = (e -> {
            if(Mode.getPropertyValue().equals(Modes.NCP)){
                if (this.lessPackets)
                    mc.timer.timerSpeed = 0.25F;
                if (this.cancelledPackets > 1) {
                    this.lessPackets = false;
                    this.cancelledPackets = 0;
                }

                if (this.lessPackets) {
                    this.cancelledPackets++;
                }
            }
            if (e.isPre())
                preY = mc.thePlayer.posY;
            if(Mode.getPropertyValue().equals(Modes.Vanilla)){
                mc.thePlayer.stepHeight = Height.getPropertyValue();
                if(mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.onGround){

                }
            }
        });
        Mode.onValueChange = () -> setModuleSuffix(Mode.getPropertyValue().name());
    }

    private boolean canStep()
    {
        ArrayList<BlockPos> collisionBlocks = new ArrayList<BlockPos>();

        Entity player = mc.thePlayer;
        BlockPos pos1 =
                new BlockPos(player.getEntityBoundingBox().minX - 0.001D,
                        player.getEntityBoundingBox().minY - 0.001D,
                        player.getEntityBoundingBox().minZ - 0.001D);
        BlockPos pos2 =
                new BlockPos(player.getEntityBoundingBox().maxX + 0.001D,
                        player.getEntityBoundingBox().maxY + 0.001D,
                        player.getEntityBoundingBox().maxZ + 0.001D);

        if(player.worldObj.isAreaLoaded(pos1, pos2))
            for(int x = pos1.getX(); x <= pos2.getX(); x++)
                for(int y = pos1.getY(); y <= pos2.getY(); y++)
                    for(int z = pos1.getZ(); z <= pos2.getZ(); z++)
                        if(y > player.posY - 1.0D && y <= player.posY)
                            collisionBlocks.add(new BlockPos(x, y, z));

        BlockPos belowPlayerPos =
                new BlockPos(player.posX, player.posY - 1.0D, player.posZ);
        for(BlockPos collisionBlock : collisionBlocks)
            if(!(player.worldObj.getBlockState(collisionBlock.add(0, 1, 0))
                    .getBlock() instanceof BlockFenceGate))
                if(player.worldObj
                        .getBlockState(collisionBlock.add(0, 1, 0))
                        .getBlock()
                        .getCollisionBoundingBox(mc.theWorld, belowPlayerPos,
                                mc.theWorld.getBlockState(collisionBlock)) != null)
                    return true;

        return true;
    }

    private enum Modes{
        Vanilla,
        NCP,
        Verus
    }
    private void ncpStep(double height) {
        List<Double> offset = Arrays.asList(0.42, 0.333, 0.248, 0.083, -0.078);
        double posX = mc.thePlayer.posX;
        double posZ = mc.thePlayer.posZ;
        double y = mc.thePlayer.posY;
        if (height < 1.1) {
            double first = 0.42;
            double second = 0.75;
            if (height != 1) {
                first *= height;
                second *= height;
                if (first > 0.425) {
                    first = 0.425;
                }
                if (second > 0.78) {
                    second = 0.78;
                }
                if (second < 0.49) {
                    second = 0.49;
                }
            }
            mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + first, posZ, false));
            if (y + second < y + height)
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + second, posZ, false));
            return;
        } else if (height < 1.6) {
            for (int i = 0; i < offset.size(); i++) {
                double off = offset.get(i);
                y += off;
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(posX, y, posZ, false));
            }
        } else if (height < 2.1) {
            double[] heights = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869};
            for (double off : heights) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
            }
        } else {
            double[] heights = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
            for (double off : heights) {
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(posX, y + off, posZ, false));
            }
        }
    }
}
