package bozoware.impl.module.visual;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.misc.TimerUtil;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.visual.EventRender3D;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.ColorProperty;
import bozoware.impl.property.ValueProperty;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayDeque;

import static org.lwjgl.opengl.GL11.*;

@ModuleData(moduleName = "BreadCrumbs", moduleCategory = ModuleCategory.VISUAL)
public class Breadcrumbs extends Module {

    @EventListener
    EventConsumer<EventRender3D> onRender3D;
    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public final BooleanProperty cleanEnabled = new BooleanProperty("Clear On Enable", true, this);
    public final BooleanProperty wallHacks = new BooleanProperty("Through Walls", false, this);
    private final ValueProperty<Float> thicc = new ValueProperty<>("Line Thickness", 2F, 1F, 10F, this);
    private final ColorProperty color = new ColorProperty("Color", new Color(0xffff0000), this);

    private final ArrayDeque<double[]> positions = new ArrayDeque<>();
    TimerUtil timer = new TimerUtil();

    public Breadcrumbs() {
        onModuleEnabled = () -> {
            if (cleanEnabled.getPropertyValue())
                positions.clear();
        };
        onUpdatePositionEvent = (e -> {
            synchronized (positions) {
                positions.add(new double[]{mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + 0.01, mc.thePlayer.posZ});
            }
            if (timer.hasReached(25)) {
                    positions.removeLast();
                    timer.reset();
            }
            timer.reset();
        });
        onRender3D = (event -> {
            final double renderPosX = mc.getRenderManager().viewerPosX;
            final double renderPosY = mc.getRenderManager().viewerPosY;
            final double renderPosZ = mc.getRenderManager().viewerPosZ;
            double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - renderPosX;
            double y = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - renderPosY;
            double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - renderPosZ;
            synchronized (positions) {
                glPushMatrix();
                glDisable(GL_TEXTURE_2D);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glEnable(GL_LINE_SMOOTH);
                glEnable(GL_BLEND);
                GL11.glLineWidth(thicc.getPropertyValue());
                if(!wallHacks.getPropertyValue()) {
                    glEnable(GL_DEPTH_TEST);
                }
                else {
                    glDisable(GL_DEPTH_TEST);
                }
                mc.entityRenderer.disableLightmap();
                glBegin(GL_LINE_STRIP);
                RenderUtil.setColor(color.getColorRGB());

                for (final double[] pos : positions)
                    glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ);

                glColor4d(1, 1, 1, 1);
                glEnd();
                glEnable(GL_DEPTH_TEST);
                glDisable(GL_LINE_SMOOTH);
                glDisable(GL_BLEND);
                glEnable(GL_TEXTURE_2D);
                glPopMatrix();
            }
        });
    }
}