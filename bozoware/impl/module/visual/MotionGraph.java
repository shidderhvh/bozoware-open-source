package bozoware.impl.module.visual;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.player.MovementUtil;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.visual.Render2DEvent;
import bozoware.impl.property.ColorProperty;
import bozoware.visual.font.MinecraftFontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

@ModuleData(moduleName = "Motion Graph", moduleCategory = ModuleCategory.VISUAL)
public class MotionGraph extends Module {

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;
    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final ColorProperty crossColor = new ColorProperty("Color", new Color(0xFFff0000), this);

    float Motion;

    public MotionGraph() {
        onRender2DEvent = (e -> {
            MinecraftFontRenderer BIFR = BozoWare.getInstance().getFontManager().BasicIcons;
            MinecraftFontRenderer BITFR = BozoWare.getInstance().getFontManager().BitFontRenderer;
            MinecraftFontRenderer arrowIcons = BozoWare.getInstance().getFontManager().ArrowIcons;
            MinecraftFontRenderer SCFR = BozoWare.getInstance().getFontManager().smallCSGORenderer;
            final ScaledResolution SR = e.getScaledResolution();
            RenderUtil.drawSmoothRoundedRect(SR.getScaledWidth() / 2 - 75, SR.getScaledHeight() / 2 + 50, SR.getScaledWidth() / 2 + 75, SR.getScaledHeight() / 2 + 125, 15, 0x60000000);
            RenderUtil.drawSmoothRoundedRect(SR.getScaledWidth() / 2 - 75, SR.getScaledHeight() / 2 + 50, SR.getScaledWidth() / 2 + 75, SR.getScaledHeight() / 2 + 65, 15, 0x60000000);

            RenderUtil.glHorizontalGradientQuad(SR.getScaledWidth() / 2 - 75, SR.getScaledHeight() / 2 + 64.5, 75, 2, 1, 0xff00ff00);
            RenderUtil.glHorizontalGradientQuad(SR.getScaledWidth() / 2, SR.getScaledHeight() / 2 + 64.5, 75, 2, 0xff00ff00, 1);

            BITFR.drawStringWithShadow("Motion Graph" + "   " + MovementUtil.getBPS(), SR.getScaledWidth() / 2 - 70, SR.getScaledHeight() / 2 + 57, -1);
            BIFR.drawStringWithShadow(getArrowText(), SR.getScaledWidth() / 2 + 55, SR.getScaledHeight() / 2 + 55, -1);
        });
        onUpdatePositionEvent = (e -> {
            Motion = Float.parseFloat(MovementUtil.getBPS());
        });
    }
    public String getArrowText() {
        if(Motion == 0){
            return "";
        }
        if(Motion > 0){
            return "b";
        }
        return "";
    }
}