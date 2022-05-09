package bozoware.visual.screens.dropdown.component.sub;

import bozoware.base.BozoWare;
import bozoware.base.module.Module;
import bozoware.base.property.Property;
import bozoware.impl.module.visual.HUD;
import bozoware.impl.property.*;
import bozoware.visual.screens.dropdown.component.Component;
import bozoware.visual.screens.dropdown.component.sub.impl.*;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;

public class ModuleButtonComponent extends Component {

    private final Module module;
    public int offset;
    public boolean expanded;
    public ArrayList<Component> subComponents = new ArrayList<>();

    public ModuleButtonComponent(Module module, int offset){
        this.module = module;
        this.offset = offset;
        int settingOffset = offset + 14;
        for (Property<?> property : BozoWare.getInstance().getPropertyManager().getPropertiesByModule(module)) {
            if (property instanceof BooleanProperty) {
                subComponents.add(new BooleanPropertyComponent((BooleanProperty) property, this, settingOffset));
                settingOffset += 14;
            }
            if (property instanceof ValueProperty) {
                subComponents.add(new ValuePropertyComponent((ValueProperty<?>) property, this, settingOffset));
                settingOffset += 14;
            }
            if (property instanceof EnumProperty) {
                subComponents.add(new EnumPropertyComponent((EnumProperty<?>) property, this, settingOffset));
                settingOffset += 14;
            }
            if (property instanceof ColorProperty) {
                subComponents.add(new ColorPropertyComponent((ColorProperty) property, this, settingOffset));
                settingOffset += 14;
            }
            if (property instanceof MultiSelectEnumProperty) {
                subComponents.add(new MultiSelectEnumPropertyComponent((MultiSelectEnumProperty<?>) property, this, settingOffset));
                settingOffset += 14;
            }
            if (property instanceof StringProperty) {
                subComponents.add(new StringPropertyComponent((StringProperty) property, this, settingOffset));
                settingOffset += 14;
            }
        }
        subComponents.add(new KeybindingComponent(module::getModuleBind, module::setModuleBind, this, settingOffset));
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {

        double y = getParentFrame().getY() + offset;

        Gui.drawRectWithWidth(getParentFrame().getX(), y, getParentFrame().frameWidth, getParentFrame().frameHeight,
                isHoveringButton(mouseX, mouseY) ? 0xff303030 : 0xff202020);

        if (module.isModuleToggled()) {
            Gui.drawRectWithWidth(getParentFrame().getX(), y, getParentFrame().frameWidth, getParentFrame().frameHeight, HUD.getInstance().bozoColor);
//            RenderUtil.glHorizontalGradientQuad(getParentFrame().getX(), y, getParentFrame().frameWidth, getParentFrame().frameHeight, HUD.getInstance().bozoColor2, HUD.getInstance().bozoColor);
        }

        BozoWare.getInstance().getFontManager().mediumFontRenderer.drawStringWithShadow(module.getModuleName(), getParentFrame().getX() + 3, y + 4, -1);

        if(expanded){
            subComponents.forEach(subComponent -> {
                if (!subComponent.isHidden())
                    subComponent.onDrawScreen(mouseX, mouseY);
            });
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isHoveringButton(mouseX, mouseY)){
            if(mouseButton == 0) {
                module.setModuleToggled(!module.isModuleToggled());
            }
            if(mouseButton == 1){
                expanded = !expanded;
                subComponents.forEach(Component::onAnimationEvent);
            }
        }
        if(expanded)
            subComponents.forEach(subComponent -> {
                if (!subComponent.isHidden())
                    subComponent.onMouseClicked(mouseX, mouseY, mouseButton);
            });
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        if(expanded)

            subComponents.forEach(subComponent -> {
                if (!subComponent.isHidden())
                    subComponent.onMouseReleased(mouseX, mouseY, mouseButton);
            });
    }

    @Override
    public void onKeyTyped(int typedKey) {
        if(expanded)
            subComponents.forEach(subComponent -> {
                if (!subComponent.isHidden())
                    subComponent.onKeyTyped(typedKey);
            });
        super.onKeyTyped(typedKey);
    }

    private boolean isHoveringButton(int mouseX, int mouseY){
        return mouseX >= getParentFrame().getX() && mouseX <= getParentFrame().getX() + getParentFrame().frameWidth
                && mouseY >= getParentFrame().getY() + offset && mouseY <= getParentFrame().getY() + offset + 13;
    }

    @Override
    public void onAnimationEvent() {
        if(expanded){
            subComponents.forEach(Component::onAnimationEvent);
        }
    }

    @Override
    public double getHeight(){
        ArrayList<Component> visibleComponents = new ArrayList<>(subComponents);
        visibleComponents.removeIf(Component::isHidden);
        if(expanded){
            double height = 14;
            for (Component component : visibleComponents) {
                height += component.getHeight();
            }
            return height;
        }
        else{
            return 14;
        }
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
        int subY = this.offset + 14;
        for(Component comp : subComponents) {
            if (!comp.isHidden()) {
                comp.setOffset(subY);
                subY += comp.getHeight();
            }
        }
    }
}
