package bozoware.visual.screens.ot.component.impl;

import bozoware.base.BozoWare;
import bozoware.base.module.Module;
import bozoware.base.util.visual.GLDraw;
import bozoware.base.util.visual.RenderUtil;
import bozoware.visual.screens.ot.component.OTComponent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class ModuleButtonComponent extends OTComponent {

    private final Module module;
    private double alphaTarget, alphaAnimation;

    private double childrenHeight;

    public double scrollValue, scrollAnimation;
    private boolean completedAnimation;

    public ModuleButtonComponent(OTComponent parentComponent, Module module, double xPosition, double yPosition, double width, double height) {
        super(parentComponent, xPosition, yPosition, width, height);
        this.module = module;
        alphaAnimation = module.isModuleToggled() ? 255 : 127;
        alphaTarget = module.isModuleToggled() ? 255 : 127;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        final double x = getParentComponent().getParentComponent().getXPosition() + getXPosition();
        final double y = getParentComponent().getParentComponent().getYPosition() + getYPosition();

        final boolean selected = BozoWare.getInstance().getGuiOTUIScreen().currentModule == this;

        if (!module.isModuleToggled())
            alphaTarget = 127;
        else
            alphaTarget = 255;

        alphaAnimation = RenderUtil.animate(alphaTarget, alphaAnimation, 0.03);

        getDefaultFontRenderer().drawStringWithShadow(module.getModuleName(), x + 2, y, new Color(255, 255, 255, (int) alphaAnimation).getRGB());

        scrollAnimation = RenderUtil.animate(scrollValue, scrollAnimation, 0.06);
        completedAnimation = Math.round(scrollAnimation) == Math.round(scrollValue);

        RenderUtil.drawSmoothRoundedRectWithWidth(getParentComponent().getParentComponent().getXPosition() + getParentComponent().getParentComponent().getWidth() - 10,
                getParentComponent().getParentComponent().getYPosition() + 40, 6, 205, 8, 0xff404040);

        Gui.drawRectWithWidth(getParentComponent().getParentComponent().getXPosition() + getParentComponent().getParentComponent().getWidth() - 10,
                getParentComponent().getParentComponent().getYPosition() + 40 + (200 * (scrollValue / (-childrenHeight + 200))), 6, 20, Color.ORANGE.getRGB());

        if (selected) {
            GLDraw.glFilledEllipse(x - 3, y + 2.5, 8, -1);
            final double normalizedScrollAnimation = completedAnimation ? Math.round(scrollAnimation) : scrollAnimation;
            GlStateManager.translate(0, normalizedScrollAnimation, 0);
            getChildrenComponents().forEach(child -> child.onDrawScreen(mouseX, mouseY));
            GlStateManager.translate(0, -normalizedScrollAnimation, 0);
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        final boolean selected = BozoWare.getInstance().getGuiOTUIScreen().currentModule == this;
        if (isHovering(mouseX, mouseY)) {
            if (mouseButton == 0) {
                module.toggleModule();
            }
            else if (mouseButton == 1) {
                BozoWare.getInstance().getGuiOTUIScreen().currentModule = this;
            }
        }
        if (selected)
            getChildrenComponents().forEach(child -> child.onMouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        final boolean selected = BozoWare.getInstance().getGuiOTUIScreen().currentModule == this;
        if (selected)
            getChildrenComponents().forEach(child -> child.onMouseReleased(mouseX, mouseY, mouseButton));
    }

    @Override
    public void onKeyTyped(int typedKey) {
        final boolean selected = BozoWare.getInstance().getGuiOTUIScreen().currentModule == this;
        if (selected)
            getChildrenComponents().forEach(child -> child.onKeyTyped(typedKey));
    }

    @Override
    public void handleMouseInput() {
        scrollValue = scrollValue + Math.signum(Mouse.getEventDWheel()) * 15;
        scrollValue = Math.min(0, (Math.max((-childrenHeight + 200), scrollValue)));
    }

    private boolean isHovering(int mouseX, int mouseY) {
        final double x = getParentComponent().getParentComponent().getXPosition() + getXPosition();
        final double y = getParentComponent().getParentComponent().getYPosition() + getYPosition();
        return mouseX >= x && mouseX <= x + getDefaultFontRenderer().getStringWidth(module.getModuleName()) && mouseY >= y && mouseY <= y + 10;
    }

    @Override
    public void addChild(OTComponent component) {
        childrenHeight += component.getHeight();
        super.addChild(component);
    }
}
