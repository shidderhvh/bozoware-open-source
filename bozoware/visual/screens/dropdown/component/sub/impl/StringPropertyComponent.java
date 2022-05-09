package bozoware.visual.screens.dropdown.component.sub.impl;

import bozoware.impl.property.StringProperty;
import bozoware.visual.screens.dropdown.component.Component;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

public class StringPropertyComponent extends Component {

    private int offset;
    private StringProperty property;
    private Component parent;
    private boolean active;

    public StringPropertyComponent(StringProperty stringProperty, Component parent, int offset) {
        this.property = stringProperty;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        Gui.drawRectWithWidth(x, y, 115, 27, 0xff151515);
        getFontRenderer().drawStringWithShadow(property.getPropertyLabel(), x + 3, y + 3, -1);

        Gui.drawRectWithWidth(x + 3, y + 14, 109, 12, isHovering(mouseX, mouseY) ? 0xff353535 : 0xff252525);

        if (active)
            Gui.drawRectWithWidth(x + 4 + getFontRenderer().getStringWidth(this.property.getPropertyValue()), y + 22, 4, 2, 0xff000000);

        getFontRenderer().drawStringWithShadow(this.property.getPropertyValue(), x + 4, y + 17, -1);

        super.onDrawScreen(mouseX, mouseY);
    }

    @Override
    public double getHeight() {
        return 27;
    }

    @Override
    public void onKeyTyped(int typedKey) {
        if (active)

        this.property.setPropertyValue(this.property.getPropertyValue() + Keyboard.getKeyName(Keyboard.getEventKey()).toLowerCase());
        super.onKeyTyped(typedKey);
    }

    private boolean isHovering(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;
        return mouseX >= x + 3 && mouseX <= x + 112 && mouseY >= y + 14 && mouseY <= y + 26;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }
}
