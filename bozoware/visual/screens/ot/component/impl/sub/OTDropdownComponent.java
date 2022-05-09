package bozoware.visual.screens.ot.component.impl.sub;

import bozoware.base.util.visual.RenderUtil;
import bozoware.visual.screens.ot.component.OTComponent;
import bozoware.visual.screens.ot.component.impl.ModuleButtonComponent;

import java.util.function.Supplier;

public class OTDropdownComponent extends OTComponent {

    private final Supplier<Enum<?>[]> enumSupplier;
    private final Supplier<Enum<?>> currentEnumValueSupplier;
    private final Runnable changeValueListenerIncrement;
    private final Runnable changeValueListenerDecrement;
    private final String dropdownLabel;

    public OTDropdownComponent(OTComponent parentComponent, String dropdownLabel,
                               Supplier<Enum<?>[]> enumSupplier, Supplier<Enum<?>> currentEnumValueSupplier,
                               Runnable changeValueListenerIncrement, Runnable changeValueListenerDecrement, double xPosition, double yPosition, double width, double height) {
        super(parentComponent, xPosition, yPosition, width, height);
        this.enumSupplier = enumSupplier;
        this.currentEnumValueSupplier = currentEnumValueSupplier;
        this.dropdownLabel = dropdownLabel;
        this.changeValueListenerIncrement = changeValueListenerIncrement;
        this.changeValueListenerDecrement = changeValueListenerDecrement;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {

        final double x = getParentComponent().getParentComponent().getParentComponent().getXPosition() + getXPosition();
        final double y = getParentComponent().getParentComponent().getParentComponent().getYPosition() + getYPosition();

        RenderUtil.setColor(0xff909090);
        getDefaultFontRenderer().drawString(dropdownLabel, x, y, 0xff909090);
        RenderUtil.drawSmoothRoundedRect((float) x, (float) y + 10, (float) (x + 190), (float) (y + 22), 4, 0xff505050);
        RenderUtil.drawSmoothRoundedRect((float) x + 0.5f, (float) y + 10.5f, (float) (x + 189.5), (float) (y + 21.5), 4, isHovering(mouseX, mouseY) ? 0xff404040 : 0xff303030);
        RenderUtil.setColor(0xff909090);
        getDefaultFontRenderer().drawString(currentEnumValueSupplier.get().name(), x + 2, y + 13, 0xff909090);
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isHovering(mouseX, mouseY)) {
            switch (mouseButton) {
                case 0:
                    changeValueListenerIncrement.run();
                    break;
                case 1:
                    changeValueListenerDecrement.run();
                    break;
            }
        }
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void onKeyTyped(int typedKey) {

    }

    private boolean isHovering(int mouseX, int mouseY) {
        final double x = getParentComponent().getParentComponent().getParentComponent().getXPosition() + getXPosition();
        final double y = getParentComponent().getParentComponent().getParentComponent().getYPosition() + getYPosition() + ((ModuleButtonComponent) (getParentComponent())).scrollValue;

        return mouseX >= x && mouseX <= x + 189.5f && mouseY >= y + 10 && mouseY <= y + 22;
    }

    @Override
    public double getHeight() {
        return 28;
    }
}
