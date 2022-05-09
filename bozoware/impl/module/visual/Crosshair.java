package bozoware.impl.module.visual;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.player.MovementUtil;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.event.visual.Render2DEvent;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.ColorProperty;
import bozoware.visual.font.MinecraftFontRenderer;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

@ModuleData(moduleName = "Crosshair", moduleCategory = ModuleCategory.VISUAL)
public class Crosshair extends Module {

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    private final BooleanProperty dynamicBool = new BooleanProperty("Dynamic", true, this);
    private final BooleanProperty renderDot = new BooleanProperty("Dot", true, this);
    private final ColorProperty crossColor = new ColorProperty("Color", new Color(0xFFff0000), this);

    public Crosshair() {
        onRender2DEvent = (e -> {
            MinecraftFontRenderer FR = BozoWare.getInstance().getFontManager().largeFontRenderer;
            MinecraftFontRenderer arrowIcons = BozoWare.getInstance().getFontManager().ArrowIcons;
            final ScaledResolution SR = e.getScaledResolution();
            if (dynamicBool.getPropertyValue()) {
                if (!MovementUtil.isMoving()) {
                    FR.drawString(".", SR.getScaledWidth() / 2 - 1.25, SR.getScaledHeight() / 2 - 7, crossColor.getColorRGB());

                    RenderUtil.drawSmoothRoundedRect(SR.getScaledWidth() / 2 - 20, SR.getScaledHeight() / 2 - 0.3F, SR.getScaledWidth() / 2 - 5, SR.getScaledHeight() / 2 + 0.5F, 0.3F, crossColor.getColorRGB());
                    RenderUtil.drawSmoothRoundedRect(SR.getScaledWidth() / 2 + 5, SR.getScaledHeight() / 2 - 0.3F, SR.getScaledWidth() / 2 + 20, SR.getScaledHeight() / 2 + 0.5F, 0.3F, crossColor.getColorRGB());
                    RenderUtil.drawSmoothRoundedRect(SR.getScaledWidth() / 2 - 0.3F, SR.getScaledHeight() / 2 - 20, SR.getScaledWidth() / 2 + 0.5F, SR.getScaledHeight() / 2 - 5, 0.3F, crossColor.getColorRGB());
                    RenderUtil.drawSmoothRoundedRect(SR.getScaledWidth() / 2 - 0.3F, SR.getScaledHeight() / 2 + 5, SR.getScaledWidth() / 2 + 0.5F, SR.getScaledHeight() / 2 + 20, 0.3F, crossColor.getColorRGB());
                } else {
                    FR.drawString(".", SR.getScaledWidth() / 2 - 1.25, SR.getScaledHeight() / 2 - 7, crossColor.getColorRGB());

                    RenderUtil.drawSmoothRoundedRect(SR.getScaledWidth() / 2 - 25, SR.getScaledHeight() / 2 - 0.3F, SR.getScaledWidth() / 2 - 10, SR.getScaledHeight() / 2 + 0.5F, 0.3F, crossColor.getColorRGB());
                    RenderUtil.drawSmoothRoundedRect(SR.getScaledWidth() / 2 + 10, SR.getScaledHeight() / 2 - 0.3F, SR.getScaledWidth() / 2 + 25, SR.getScaledHeight() / 2 + 0.5F, 0.3F, crossColor.getColorRGB());
                    RenderUtil.drawSmoothRoundedRect(SR.getScaledWidth() / 2 - 0.3F, SR.getScaledHeight() / 2 - 25, SR.getScaledWidth() / 2 + 0.5F, SR.getScaledHeight() / 2 - 10, 0.3F, crossColor.getColorRGB());
                    RenderUtil.drawSmoothRoundedRect(SR.getScaledWidth() / 2 - 0.3F, SR.getScaledHeight() / 2 + 10, SR.getScaledWidth() / 2 + 0.5F, SR.getScaledHeight() / 2 + 25, 0.3F, crossColor.getColorRGB());
                }
            }
            else {
                FR.drawString(".", SR.getScaledWidth() / 2 - 1.25, SR.getScaledHeight() / 2 - 7, crossColor.getColorRGB());

                RenderUtil.drawSmoothRoundedRect(SR.getScaledWidth() / 2 - 20, SR.getScaledHeight() / 2 - 0.3F, SR.getScaledWidth() / 2 - 5, SR.getScaledHeight() / 2 + 0.5F, 0.3F, crossColor.getColorRGB());
                RenderUtil.drawSmoothRoundedRect(SR.getScaledWidth() / 2 + 5, SR.getScaledHeight() / 2 - 0.3F, SR.getScaledWidth() / 2 + 20, SR.getScaledHeight() / 2 + 0.5F, 0.3F, crossColor.getColorRGB());
                RenderUtil.drawSmoothRoundedRect(SR.getScaledWidth() / 2 - 0.3F, SR.getScaledHeight() / 2 - 20, SR.getScaledWidth() / 2 + 0.5F, SR.getScaledHeight() / 2 - 5, 0.3F, crossColor.getColorRGB());
                RenderUtil.drawSmoothRoundedRect(SR.getScaledWidth() / 2 - 0.3F, SR.getScaledHeight() / 2 + 5, SR.getScaledWidth() / 2 + 0.5F, SR.getScaledHeight() / 2 + 20, 0.3F, crossColor.getColorRGB());
            }
        });
    }
}
