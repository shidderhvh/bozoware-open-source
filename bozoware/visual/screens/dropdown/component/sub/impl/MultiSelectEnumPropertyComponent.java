package bozoware.visual.screens.dropdown.component.sub.impl;

import bozoware.base.BozoWare;
import bozoware.impl.property.MultiSelectEnumProperty;
import bozoware.visual.screens.dropdown.component.Component;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;


public class MultiSelectEnumPropertyComponent extends Component {

    private int offset;
    private final MultiSelectEnumProperty property;
    private final Component parent;

    private boolean expanded;

    public MultiSelectEnumPropertyComponent(MultiSelectEnumProperty<?> property, Component parent, int offset){
        this.offset = offset;
        this.property = property;
        this.parent = parent;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        Gui.drawRectWithWidth(x, y, 115, 14, isMouseHovering(mouseX, mouseY) ? 0xff101010 : 0xff151515);

        getFontRenderer().drawStringWithShadow(property.getPropertyLabel(), x + 3, y + 4, -1);
        final String suffixText = property.getSelectedValues().size() + " Selected";
        getFontRenderer().drawStringWithShadow(suffixText,
                x + 115 - BozoWare.getInstance().getFontManager().smallFontRenderer.getStringWidth(suffixText) - 8, y + 4, 0xff999999);

        if (expanded) {
            Gui.drawRectWithWidth(x + 5, y + 15, 105, property.getConstantEnumValues().length * 15, 0xff252525);
            for (int i = 0; i < property.getConstantEnumValues().length; i++) {
                GlStateManager.color(1, 1, 1);
                getFontRenderer().drawCenteredString(property.getConstantEnumValues()[i].name(), (float) (x + (115/2)), (float) (y + 19 + i * 15),
                        property.isSelected(i) ? -1 : 0xff909090);
            }
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;
        if(isMouseHovering(mouseX, mouseY)) {
            if (mouseButton == 1) {
                expanded = !expanded;
            }
        }
        if (expanded) {
            for (int i = 0; i < property.getConstantEnumValues().length; i++) {
                if (mouseX >= x + 5 && mouseY >= y + 19 + i * 15 && mouseY <= y + 33 + i * 15 && mouseX <= x + 110)
                    property.setValue(i, MultiSelectEnumProperty.MultiOption.TOGGLE);
            }
        }
    }

    @Override
    public boolean isHidden() {
        return property.isHidden();
    }

    private boolean isMouseHovering(int mouseX, int mouseY){
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        return mouseX >= x && mouseX <= x + 115 && mouseY >= y && mouseY <= y + 14;
    }

    @Override
    public double getHeight() {
        return expanded ? (property.getConstantEnumValues().length * 15 + 16) : 14;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
