package bozoware.visual.screens.dropdown.component.sub.impl;

import bozoware.base.BozoWare;
import bozoware.base.util.misc.MathUtil;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.module.visual.HUD;
import bozoware.impl.property.ValueProperty;
import bozoware.visual.screens.dropdown.component.Component;
import bozoware.visual.screens.dropdown.component.sub.ModuleButtonComponent;
import net.minecraft.client.gui.Gui;


public class ValuePropertyComponent extends Component {

    private final ValueProperty property;
    private final ModuleButtonComponent parent;
    private int offset;
    private double sliderAnimation;
    private boolean isSliding;

    public ValuePropertyComponent(ValueProperty property, ModuleButtonComponent parent, int offset){
        this.property = property;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

//        Gui.drawRectWithWidth(x, y, 115, 14, isMouseOverSlider(mouseX, mouseY) ? 0xff101010 : 0xff151515);

//        RenderUtil.glHorizontalGradientQuad(x, y, 115, 14, HUD.getInstance().bozoColor2, HUD.getInstance().getColor2Gradient());


        if (isSliding && isMouseOverSlider(mouseX, mouseY)) {
            double max = property.getMaximumValue().doubleValue();
            double min = property.getMinimumValue().doubleValue();
            double mousePercent = (mouseX - (x)) / (115);
            double valueToSet = MathUtil.linearInterpolate(min, max, mousePercent);
            if (property.getPropertyValue() instanceof Double) {
                property.setPropertyValue(MathUtil.roundToPlace(valueToSet, 2));
            }
            if (property.getPropertyValue() instanceof Integer) {
                property.setPropertyValue((int) MathUtil.roundToPlace(valueToSet, 2));
            }
            if (property.getPropertyValue() instanceof Float) {
                property.setPropertyValue((float) MathUtil.roundToPlace(valueToSet, 2));
            }
            if(property.getPropertyValue() instanceof Long){
                property.setPropertyValue((long) MathUtil.roundToPlace(valueToSet, 2));
            }
            if(property.getPropertyValue() instanceof Byte){
                property.setPropertyValue((byte) MathUtil.roundToPlace(valueToSet, 2));
            }
        }

        double sliderRectPercentage = ((property.getPropertyValue().doubleValue() - property.getMinimumValue().doubleValue()) / (property.getMaximumValue().doubleValue() - property.getMinimumValue().doubleValue())) *
                (parent.getParentFrame().frameWidth - 2);

        if(isSliding){
            sliderAnimation = RenderUtil.animate(sliderRectPercentage, sliderAnimation, 0.04);
        }
        else {
            sliderAnimation = sliderRectPercentage;
        }

        Gui.drawRectWithWidth(x + 1, (float) y + 1, sliderAnimation, 12, HUD.getInstance().bozoColor);

        String currentValue = null;
        if (property.getPropertyValue() instanceof Integer || property.getPropertyValue() instanceof Long) {
            currentValue = property.getPropertyValue().toString();
        } else if (property.getPropertyValue() instanceof Float || property.getPropertyValue() instanceof Double) {
            currentValue = String.format("%.1f", property.getPropertyValue().doubleValue());
        }

        getFontRenderer().drawStringWithShadow(property.getPropertyLabel(), x + 3, y + 4, -1);
        getFontRenderer().drawStringWithShadow("\2477" + currentValue, x + 112 - BozoWare.getInstance().getFontManager().smallFontRenderer.getStringWidth(currentValue), y + 4, -1);

        super.onDrawScreen(mouseX, mouseY);
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isMouseOverSlider(mouseX, mouseY) && mouseButton == 0) {
            isSliding = true;
        }
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        isSliding = false;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public boolean isHidden() {
        return property.isHidden();
    }

    private boolean isMouseOverSlider(int mouseX, int mouseY){
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;
        return mouseX >= x && mouseX <= x + 115
                && mouseY >= y && mouseY <= y + 14;
    }

}
