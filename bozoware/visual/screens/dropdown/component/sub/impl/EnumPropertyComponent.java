package bozoware.visual.screens.dropdown.component.sub.impl;

import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.property.EnumProperty;
import bozoware.visual.screens.dropdown.component.Component;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;


public class EnumPropertyComponent extends Component {

    private int offset;
    private final EnumProperty property;
    private final Component parent;
    private double enumAnimation;
    private boolean expanded;

    public EnumPropertyComponent(EnumProperty<?> property, Component parent, int offset){
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
        getFontRenderer().drawStringWithShadow(property.getPropertyValue().toString(),
                x + 115 - getFontRenderer().getStringWidth(property.getPropertyValue().toString()) - 3, y + 4, 0xff999999);

        enumAnimation = RenderUtil.animate(property.getEnumValues().length * 15, enumAnimation, 0.04);

        if (expanded) {
            Gui.drawRectWithWidth(x + 5, y + 15, 105, enumAnimation, 0xff252525);
            for (int i = 0; i < property.getEnumValues().length; i++) {
                GlStateManager.color(1, 1, 1);
                getFontRenderer().drawCenteredString(property.getEnumValues()[i].name(), (float) (x + (115/2)), (float) (y + 19 + i * 15),
                        property.getPropertyValue() == property.getEnumValues()[i] ? -1 : 0xff909090);
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
            for (int i = 0; i < property.getEnumValues().length; i++) {
                if (mouseX >= x + 5 && mouseY >= y + 19 + i * 15 && mouseY <= y + 33 + i * 15 && mouseX <= x + 110)
                    property.setPropertyValue(property.getEnumValues()[i]);
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
        return expanded ? (property.getEnumValues().length * 15 + 16) : 14;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
