package bozoware.impl.module.visual;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.visual.EventRender3D;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Sphere;

@ModuleData(moduleName = "Penis", moduleCategory = ModuleCategory.VISUAL)
public class Penis extends Module {

    @EventListener
    EventConsumer<EventRender3D> onRender3D;
    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public Penis() {
        onModuleEnabled = () -> {

        };
        onUpdatePositionEvent = (e -> {

        });
        onRender3D = (event -> {
            esp(mc.thePlayer, mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + 1, mc.thePlayer.posZ);
        });
    }
    public void esp(EntityPlayer player, double x, double y, double z) {
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(true);
        GL11.glLineWidth(1.0F);

        GL11.glRotatef(-player.rotationYaw, 0.0F, player.height, 0.0F);
        GL11.glTranslated(-x, -y, -z);
        GL11.glTranslated(x, y + (double)(player.height / 2.0F) - 0.22499999403953552D, z);
        GL11.glColor4f(0.225F, 0.172F, 0.150F, 1.0F);

        int lines = 180;

        GL11.glTranslated(0.0D, 0.0D, 0.07500000298023224D);
        Cylinder shaft = new Cylinder();
        shaft.setDrawStyle(100013);
        shaft.draw(0.1F, 0.11F, 0.4F, 180, lines);
        GL11.glColor4f(0.240F, 0.184F, 0.160F, 1.0F);
        GL11.glTranslated(0.0D, 0.0D, -0.12500000298023223D);
        GL11.glTranslated(-0.09000000074505805D, 0.0D, 0.0D);

        Sphere right = new Sphere();
        right.setDrawStyle(100013);
        right.draw(0.14F, 10, lines);
        GL11.glTranslated(0.16000000149011612D, 0.0D, 0.0D);

        Sphere left = new Sphere();
        left.setDrawStyle(100013);
        left.draw(0.14F, 10, lines);
        GL11.glTranslated(-0.07000000074505806D, 0.0D, 0.589999952316284D);

        Sphere tip = new Sphere();
        tip.setDrawStyle(100013);
        tip.draw(0.13F, 45, lines);
        GL11.glColor4f(1, 1, 1, 1);

        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
    }
}
