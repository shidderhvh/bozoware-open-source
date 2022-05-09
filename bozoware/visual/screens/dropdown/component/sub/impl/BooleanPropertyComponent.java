package bozoware.visual.screens.dropdown.component.sub.impl;

import bozoware.base.BozoWare;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.module.visual.HUD;
import bozoware.impl.property.BooleanProperty;
import bozoware.visual.screens.dropdown.component.Component;
import bozoware.visual.screens.dropdown.component.sub.ModuleButtonComponent;
import net.minecraft.client.gui.Gui;

public class BooleanPropertyComponent extends Component {

    private final ModuleButtonComponent parent;
    private final BooleanProperty property;
    private int offset;
    private double boolAnimation;

    public BooleanPropertyComponent(BooleanProperty property, ModuleButtonComponent parent, int offset){
        this.parent = parent;
        this.property = property;
        this.offset = offset;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        Gui.drawRectWithWidth(x, y, 115, 14, isMouseHovering(mouseX, mouseY) ? 0xff101010 : 0xff151515);

        BozoWare.getInstance().getFontManager().mediumFontRenderer.drawStringWithShadow(property.getPropertyLabel(), x + 3, y + 4, -1);
        Gui.drawRectWithWidth(x + 115 - 28, y + 2, 26, 10, 0x99000000);

        boolAnimation = RenderUtil.animate((property.getPropertyValue() ? 12 : 0), boolAnimation, 0.04);

        Gui.drawRectWithWidth(x + 115 - 27 + boolAnimation, y + 3, 12, 8, property.getPropertyValue() ? HUD.getInstance().bozoColor : 0xff202020);
//        Gui.drawRectWithWidth(x + 115 - 27 + (property.getPropertyValue() ? 12 : 0), y + 3, 12, 8, property.getPropertyValue() ? HUD.getInstance().bozoColor : 0xff202020);
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isMouseHovering(mouseX, mouseY)){
            property.setPropertyValue(!property.getPropertyValue());
        }
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
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
}
