package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.base.util.misc.TimerUtil;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.visual.EventRender3D;
import bozoware.impl.event.visual.Render2DEvent;
import net.minecraft.block.BlockBed;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.glPushMatrix;

@ModuleData(moduleName = "Bed Breaker", moduleCategory = ModuleCategory.PLAYER)
public class BedBreaker extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<EventRender3D> onRender2DEvent;


    public BedBreaker(){
        onRender2DEvent = (e -> {
            for(int x = -7; x < 7; x++) {
                for (int y = -7; y < 7; y++) {
                    for (int z = -7; z < 7; z++) {
                        BlockPos pos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                        if (pos.getBlock() instanceof BlockBed) {
                            System.out.println("breaking");
//                            line(pos, false, -1);
                        }
                    }
                }
            }

        });
        onUpdatePositionEvent = (e -> {
        if (e.isPre()) {
            for(int x = -7; x < 7; x++) {
                for(int y = -7; y < 7; y++) {
                    for(int z = -7; z < 7; z++) {
                        BlockPos pos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                        if (pos.getBlock() instanceof BlockBed) {
                            float[] rot = getRotationsToBlock(pos, EnumFacing.NORTH);
                            e.setYaw(rot[0]);
                            e.setPitch(rot[1]);
                            mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.NORTH));
                            TimerUtil timer = new TimerUtil();
                            if(timer.hasReached(250L) && !mc.isSingleplayer() && mc.getCurrentServerData().serverIP.contains("hypixel.net")) {
                                Wrapper.sendPacketDirect(new C03PacketPlayer.C04PacketPlayerPosition(pos.getX(), pos.getY() - 1, pos.getZ(), true));
                                timer.reset();
                            }
                            mc.thePlayer.swingItem();
                            mc.getNetHandler().getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.NORTH));
                        }
                    }
                }
            }
        }
    });
    }
    public float[] getRotationsToBlock(BlockPos paramBlockPos, EnumFacing paramEnumFacing) {
        double d1 = paramBlockPos.getX() + 0.5D - mc.thePlayer.posX + paramEnumFacing.getFrontOffsetX() / 2.0D;
        double d2 = paramBlockPos.getZ() + 0.5D - mc.thePlayer.posZ + paramEnumFacing.getFrontOffsetZ() / 2.0D;
        double d3 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - (paramBlockPos.getY() + 0.5D);
        double d4 = MathHelper.sqrt_double(d1 * d1 + d2 * d2);
        float f1 = (float) (Math.atan2(d2, d1) * 180.0D / Math.PI) - 90.0F;
        float f2 = (float) (Math.atan2(d3, d4) * 180.0D / Math.PI);
        if (f1 < 0.0F) {
            f1 += 360.0F;
        }
        return new float[]{f1, f2};
    }
    private void line(BlockPos pos, boolean CustomColor, int Color) {
        glPushMatrix();
        GL11.glLoadIdentity();
        mc.entityRenderer.orientCamera(mc.timer.renderPartialTicks);
        GL11.glDisable(2929);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        x -= RenderManager.renderPosX;
        y -= RenderManager.renderPosY;
        z -= RenderManager.renderPosZ;
        if(CustomColor)
            RenderUtil.setColorWithAlpha(Color, 255);
        GL11.glLineWidth(1.5F);
        GL11.glBegin(3);
        GL11.glVertex3d(0.0D, (double)mc.thePlayer.getEyeHeight(), 0.0D);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glColor3d(1.0D, 1.0D, 1.0D);
        GL11.glPopMatrix();
    }
}
