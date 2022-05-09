package bozoware.visual.screens.ot.component;

import bozoware.base.BozoWare;
import bozoware.visual.font.MinecraftFontRenderer;

import java.util.ArrayList;

public abstract class OTComponent {

    private final ArrayList<OTComponent> childrenComponents = new ArrayList<>();

    private final OTComponent parentComponent;

    private double xPosition, yPosition;
    private final double width, height;

    public OTComponent(OTComponent parentComponent, double xPosition, double yPosition, double width, double height){
        this.parentComponent = parentComponent;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.width = width;
        this.height = height;
    }

    public abstract void onDrawScreen(int mouseX, int mouseY);

    public abstract void onMouseClicked(int mouseX, int mouseY, int mouseButton);

    public abstract void onMouseReleased(int mouseX, int mouseY, int mouseButton);

    public abstract void onKeyTyped(int typedKey);

    public void handleMouseInput() {

    }

    public double getXPosition() {
        return xPosition;
    }

    public double getYPosition() {
        return yPosition;
    }

    public void setXPosition(double xPosition) {
        this.xPosition = xPosition;
    }

    public void setYPosition(double yPosition) {
        this.yPosition = yPosition;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public OTComponent getParentComponent() {
        return parentComponent;
    }

    public ArrayList<OTComponent> getChildrenComponents() {
        return childrenComponents;
    }

    public void addChild(OTComponent component) {
        childrenComponents.add(component);
    }

    public MinecraftFontRenderer getDefaultFontRenderer() {
        return BozoWare.getInstance().getFontManager().onetapDefaultRenderer;
    }

    public MinecraftFontRenderer getIconsFontRenderer() {
        return BozoWare.getInstance().getFontManager().onetapIconsRenderer;
    }
}
