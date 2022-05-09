package bozoware.visual.screens.dropdown.component.sub.impl;

import bozoware.base.util.visual.ColorUtil;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.property.ColorProperty;
import bozoware.visual.screens.dropdown.component.Component;
import bozoware.visual.screens.dropdown.component.sub.ModuleButtonComponent;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ColorPropertyComponent extends Component {

    private int offset;
    private final ModuleButtonComponent parent;
    private final ColorProperty property;
    private boolean active;

    public ColorPropertyComponent(ColorProperty property, ModuleButtonComponent parent, int offset) {
        this.property = property;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        Gui.drawRectWithWidth(x, y, 115, 14, 0xff151515);

        final float[] hsb = Color.RGBtoHSB(property.getPropertyValue().getRed(),
                property.getPropertyValue().getGreen(),
                property.getPropertyValue().getBlue(), null);

        drawHueSlider(x, y);
        Gui.drawRectWithWidth(x + (hsb[0] * 115), y, 2, 14, 0xff000000);
        Gui.drawRectWithWidth(x + (hsb[0] * 115) + 0.5, y + 0.5, 1, 13, -1);

        getFontRenderer().drawStringWithShadow(property.getPropertyLabel(), x + 3, y + 4, -1);

        if (!isHovering(mouseX, mouseY))
            active = false;

        if (active) {
            final Color newColor = new Color(Color.HSBtoRGB((float) ((mouseX - x) / 115), hsb[1], hsb[2]));
            final Color settingColor = new Color(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), property.getPropertyValue().getAlpha());
            property.setPropertyValue(settingColor);
        }

        super.onDrawScreen(mouseX, mouseY);
    }

    private static void drawHueSlider(final double x,
                                      final double y) {
        GL11.glTranslated(x, y, 0);
        final int[] colours = {
                0xFFFF0000, // red (255, 0, 0)
                0xFFFFFF00, // yellow (255, 255, 0)
                0xFF00FF00, // green (0, 255, 0)
                0xFF00FFFF, // aqua (0, 255, 255)
                0xFF0000FF, // blue (0, 0, 255)
                0xFFFF00FF, // purple (255, 0, 255)
                0xFFFF0000, // red (255, 0, 0)
        };
        final double segment = (double) 115 / colours.length;
        for (int i = 0; i < colours.length; i++) {
            final int colour = colours[i];
            final int top = i != 0 ? ColorUtil.interpolateColors(new Color(colours[i - 1]), new Color(colour), 0.5f).getRGB() : colour;
            final int bottom = i + 1 < colours.length ? ColorUtil.interpolateColors(new Color(colour), new Color(colours[i + 1]), 0.5f).getRGB() : colour;
            final double start = segment * i;
            RenderUtil.glHorizontalGradientQuad(start, 0, segment, 14, top, bottom);
        }
        GL11.glTranslated(-x, -y, 0);
    }

    private boolean isHovering(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;
        return mouseX >= x && mouseX <= x + 115 && mouseY >= y && mouseY <= y + 14;
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovering(mouseX, mouseY)) {
            active = true;
        }
    }

    @Override
    public boolean isHidden() {
        return property.isHidden();
    }

    @Override
    public double getHeight() {
        return 28;
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        active = false;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
