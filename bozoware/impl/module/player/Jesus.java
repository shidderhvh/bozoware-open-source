package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.block.EventAABB;
import bozoware.impl.event.player.UpdatePositionEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

@ModuleData(moduleName = "Jesus", moduleCategory = ModuleCategory.PLAYER)
public class Jesus extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<EventAABB> onEventAABB;
    static boolean wasWater;

    public Jesus(){
        onUpdatePositionEvent = (event -> {
            if (event.isPre()) {
                if (isOnLiquid()) {
                    event.setOnGround(false);
                }
                if (!event.isPre() || (mc.thePlayer.isBurning() && isOnWater())) return;
                if (isInLiquid() && !mc.gameSettings.keyBindSneak.isKeyDown() && !mc.gameSettings.keyBindJump.isKeyDown() && mc.thePlayer.fallDistance < 3) {
                    mc.thePlayer.motionY = 0.1;
                }
            }
        });
        onEventAABB = (event -> {
            Block block = mc.theWorld.getBlockState(event.getPos()).getBlock();
            if (mc.theWorld == null || mc.thePlayer.fallDistance > 3 || (mc.thePlayer.isBurning() && isOnWater()))
                return;
            if (!(block instanceof BlockLiquid) || isInLiquid() || mc.thePlayer.isSneaking())
                return;
            event.setBoundingBox(new AxisAlignedBB(0, 0, 0, 1, 1, 1).contract(0, 0, 0).offset(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()));
        });
    }
    public static boolean isOnLiquid() {
        final double y = mc.thePlayer.posY - 0.03;
        for (int x = MathHelper.floor_double(mc.thePlayer.posX); x < MathHelper.ceiling_double_int(mc.thePlayer.posX); ++x) {
            for (int z = MathHelper.floor_double(mc.thePlayer.posZ); z < MathHelper.ceiling_double_int(mc.thePlayer.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, MathHelper.floor_double(y), z);
                if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isInLiquid() {
        final double y = mc.thePlayer.posY + 0.01;
        for (int x = MathHelper.floor_double(mc.thePlayer.posX); x < MathHelper.ceiling_double_int(mc.thePlayer.posX); ++x) {
            for (int z = MathHelper.floor_double(mc.thePlayer.posZ); z < MathHelper.ceiling_double_int(mc.thePlayer.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, (int) y, z);
                if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean isOnWater() {
        final double y = mc.thePlayer.posY - 0.03;
        for (int x = MathHelper.floor_double(mc.thePlayer.posX); x < MathHelper.ceiling_double_int(mc.thePlayer.posX); ++x) {
            for (int z = MathHelper.floor_double(mc.thePlayer.posZ); z < MathHelper.ceiling_double_int(mc.thePlayer.posZ); ++z) {
                final BlockPos pos = new BlockPos(x, MathHelper.floor_double(y), z);
                if (mc.theWorld.getBlockState(pos).getBlock() instanceof BlockLiquid && mc.theWorld.getBlockState(pos).getBlock().getMaterial() == Material.water) {
                    return true;
                }
            }
        }
        return false;
    }
}
