
package bozoware.impl.module.visual;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.event.visual.EventRender3D;
import bozoware.impl.property.ColorProperty;
import bozoware.impl.property.ValueProperty;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;

import java.awt.*;


@ModuleData(moduleName = "ChinaHat", moduleCategory = ModuleCategory.VISUAL)
public class ChinaHat extends Module {

    @EventListener
    EventConsumer<EventRender3D> onEventRender3D;

    private final ValueProperty<Float> Width = new ValueProperty<>("Width", 0.86F, 0F, 1F, this);
    private final ValueProperty<Float> Height = new ValueProperty<>("Height", 0.30F, 0F, 1F, this);
    private final ValueProperty<Integer> Sides = new ValueProperty<>("Sides", 30, 3, 90, this);
    private final ColorProperty color = new ColorProperty("Color", new Color(0xffff0000), this);
    private final ValueProperty<Integer> alpha = new ValueProperty<>("Opacity", 100, 0, 255, this);

    public ChinaHat() {
        onEventRender3D = (EventRender3D -> {
            GlStateManager.pushMatrix();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            RenderUtil.setColorWithAlpha(color.getColorRGB(), alpha.getPropertyValue());
            GlStateManager.disableCull();
            if(!mc.thePlayer.isSneaking()){
                GlStateManager.translate(0f, mc.thePlayer.height + 0.3F, 0f);
            }
            else{
                GlStateManager.translate(0f, mc.thePlayer.height + 0.1F, 0f);
            }
            GlStateManager.rotate(90f, 1f, 0f, 0f);

            Cylinder cylinder = new Cylinder();
            cylinder.setDrawStyle(GLU.GLU_FILL);
            cylinder.draw(0.0f, Width.getPropertyValue(), Height.getPropertyValue(), Sides.getPropertyValue(), 69);

            GlStateManager.disableColorMaterial();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GlStateManager.color(1, 1, 1, 255);
            GlStateManager.popMatrix();
        });
    }
}
