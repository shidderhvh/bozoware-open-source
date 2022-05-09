package bozoware.impl.module.visual;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.event.visual.EventRender3D;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleData(moduleName = "Chest ESP", moduleCategory = ModuleCategory.WORLD)

public class ChestESP extends Module {

    @EventListener
    EventConsumer<EventRender3D> onRender3D;

    public ChestESP() {
        onRender3D = (r3D -> {
            for(TileEntity TE : mc.theWorld.loadedTileEntityList){
                if (canRender(TE)) {
                    double posX = TE.getPos().getX() - RenderManager.renderPosX;
                    double posY = TE.getPos().getY() - RenderManager.renderPosY;
                    double posZ = TE.getPos().getZ() - RenderManager.renderPosZ;
                    AxisAlignedBB bb = new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.94, 0.875, 0.94).offset(posX, posY, posZ);
                    drawBlockESP(bb, HUD.getInstance().bozoColorColor, 255, 5);
                }
            }
        });
    }
    public boolean canRender(TileEntity e) {
        return e instanceof TileEntityChest && mc.thePlayer != null;
    }
    private void drawBlockESP(AxisAlignedBB bb, Color color, int alpha, float width) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        RenderUtil.setColorWithAlpha(color.getRGB(), alpha);
        GL11.glLineWidth(width);
        RenderUtil.setColorWithAlpha(color.getRGB(), alpha);
        RenderUtil.drawOutlinedBox(bb);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
}