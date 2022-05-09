package bozoware.visual.screens.ot.component.impl;

import bozoware.base.BozoWare;
import bozoware.base.util.visual.RenderUtil;
import bozoware.visual.screens.ot.component.OTComponent;

import java.awt.*;

public class TabSelectorComponent extends OTComponent {

    private final String tabName;
    private final String tabIcon;

    public double alphaAnimation = 127, alphaTarget;

    public TabSelectorComponent(OTComponent parentComponent, String tabName, String tabIcon, double xPosition, double yPosition, double width, double height) {
        super(parentComponent, xPosition, yPosition, width, height);
        this.tabName = tabName;
        this.tabIcon = tabIcon;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        final double posX = getParentComponent().getXPosition() + getXPosition();
        final double posY = getParentComponent().getYPosition() + getYPosition();
        final boolean selected = BozoWare.getInstance().getGuiOTUIScreen().currentTabComponent == this;

        if (!selected)
            alphaTarget = 120;

        alphaAnimation = RenderUtil.animate(alphaTarget, alphaAnimation, 0.03);

        getIconsFontRenderer().drawStringWithShadow(tabIcon, posX,
                posY + 0.5, new Color(255, 255, 255, (int) alphaAnimation).getRGB());
        getDefaultFontRenderer().drawStringWithShadow(tabName, posX + 11, posY, new Color(255, 255, 255, (int) alphaAnimation).getRGB());

        if (selected) {
            RenderUtil.startScissor();
            RenderUtil.scissor(getParentComponent().getXPosition(), getParentComponent().getYPosition() + 36, getParentComponent().getWidth(), getParentComponent().getHeight() - 36);
            getChildrenComponents().forEach(child -> child.onDrawScreen(mouseX, mouseY));
            RenderUtil.endScissor();
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        final boolean selected = BozoWare.getInstance().getGuiOTUIScreen().currentTabComponent == this;
        if (isHovering(mouseX, mouseY) && mouseButton == 0) {
            BozoWare.getInstance().getGuiOTUIScreen().currentTabComponent = this;
            BozoWare.getInstance().getGuiOTUIScreen().currentModule = (ModuleButtonComponent) getChildrenComponents().get(0);
            alphaTarget = 255;
        }
        if (selected)
            getChildrenComponents().forEach(child -> child.onMouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        final boolean selected = BozoWare.getInstance().getGuiOTUIScreen().currentTabComponent == this;
        if (selected)
            getChildrenComponents().forEach(child -> child.onMouseReleased(mouseX, mouseY, mouseButton));
    }

    @Override
    public void onKeyTyped(int typedKey) {

    }

    @Override
    public void handleMouseInput() {
        final boolean selected = BozoWare.getInstance().getGuiOTUIScreen().currentTabComponent == this;
        if (selected)
            getChildrenComponents().forEach(OTComponent::handleMouseInput);
    }

    public boolean isHovering(int mouseX, int mouseY) {
        final double posX = getParentComponent().getXPosition() + getXPosition();
        final double posY = getParentComponent().getYPosition() + getYPosition();
        return mouseX >= posX && mouseX <= posX + 11 + getDefaultFontRenderer().getStringWidth(tabName) && mouseY >= posY && mouseY <= posY + 10;
    }
}
