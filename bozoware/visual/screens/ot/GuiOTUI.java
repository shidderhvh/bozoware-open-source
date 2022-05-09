package bozoware.visual.screens.ot;

import bozoware.base.BozoWare;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.property.Property;
import bozoware.base.util.visual.GLDraw;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.module.combat.Aura;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.EnumProperty;
import bozoware.impl.property.ValueProperty;
import bozoware.visual.screens.ot.component.OTComponent;
import bozoware.visual.screens.ot.component.impl.ModuleButtonComponent;
import bozoware.visual.screens.ot.component.impl.TabSelectorComponent;
import bozoware.visual.screens.ot.component.impl.sub.OTCheckboxComponent;
import bozoware.visual.screens.ot.component.impl.sub.OTDropdownComponent;
import bozoware.visual.screens.ot.component.impl.sub.OTSliderComponent;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class GuiOTUI extends GuiScreen {

    public OTComponent rootComponent;
    private double rootDistX, rootDistY;
    private boolean draggingRoot;
    public final ArrayList<OTComponent> components = new ArrayList<>();
    public TabSelectorComponent currentTabComponent;
    public ModuleButtonComponent currentModule;

    @SuppressWarnings("all")
    public void setupGui() {
        this.rootComponent = new OTComponent(null, 50, 50, 300, 250) {
            @Override
            public void onDrawScreen(int mouseX, int mouseY) {
                RenderUtil.drawSmoothRoundedRect((float) getXPosition(),
                        (float) getYPosition(),
                        (float) (getXPosition() + getWidth()),
                        (float) (getYPosition() + getHeight()), 15, 0xff151515);
                RenderUtil.drawSmoothRoundedRect((float) getXPosition(), (float) getYPosition(),
                        (float) (getXPosition() + getWidth()),
                        (float) (getYPosition() + 30), 8, Color.ORANGE.getRGB());
                GLDraw.glFilledQuad(getXPosition() - 0.5, getYPosition() + 3, getWidth() + 1, 32, 0xff151515);

                RenderUtil.setColor(-1);
                BozoWare.getInstance().getFontManager().onetapFontRenderer.drawString("onetap", getXPosition() + 8, getYPosition() + 14, -1);

                //Padding and separators
                GLDraw.glFilledQuad(getXPosition(), getYPosition() + 35, getWidth(), 0.5, 0xff505050);
                GLDraw.glFilledQuad(getXPosition() + 70, getYPosition() + 36, 0.5, getHeight() - 36, 0xff505050);

                if (draggingRoot) {
                    setXPosition(mouseX - rootDistX);
                    setYPosition(mouseY - rootDistY);
                }

                getChildrenComponents().forEach(child -> child.onDrawScreen(mouseX, mouseY));
            }

            @Override
            public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
                if (mouseX >= getXPosition() && mouseX <= getXPosition() + getWidth() &&
                        mouseY >= getYPosition() && mouseY <= getYPosition() + 4 && mouseButton == 0) {
                    draggingRoot = true;
                    rootDistX = mouseX - getXPosition();
                    rootDistY = mouseY - getYPosition();
                }
                getChildrenComponents().forEach(child -> child.onMouseClicked(mouseX, mouseY, mouseButton));
            }

            @Override
            public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
                draggingRoot = false;
                getChildrenComponents().forEach(child -> child.onMouseReleased(mouseX, mouseY, mouseButton));
            }

            @Override
            public void onKeyTyped(int typedKey) {
                getChildrenComponents().forEach(child -> child.onKeyTyped(typedKey));
            }

            @Override
            public void handleMouseInput() {
                getChildrenComponents().forEach(child -> child.handleMouseInput());
            }
        };
        int offset = 0;
        for (int i = 0; i < ModuleCategory.values().length; i++) {
            ModuleCategory currentCategory = ModuleCategory.values()[i];
            TabSelectorComponent tab = new TabSelectorComponent(rootComponent, currentCategory.categoryName, currentCategory.iconCode, 55 + offset, 17, 0, 0);
            for (int i2 = 0; i2 < BozoWare.getInstance().getModuleManager().getModulesByCategory(currentCategory).size(); i2++) {
                Module currentIteratedModule = BozoWare.getInstance().getModuleManager().getModulesByCategory(currentCategory).get(i2);
                ModuleButtonComponent moduleButtonComponent = new ModuleButtonComponent(tab, currentIteratedModule, 8, 50 + i2 * 14, 0, 0);
                if (currentIteratedModule.equals(BozoWare.getInstance().getModuleManager().getModuleByClass.apply(Aura.class)))
                    currentModule = moduleButtonComponent;
                double propertyOffset = 45;
                for (int i3 = 0; i3 < currentIteratedModule.getModuleProperties().size(); i3++) {
                    final Property<?> currentProperty = currentIteratedModule.getModuleProperties().get(i3);
                    if (currentProperty instanceof ValueProperty) {
                        final ValueProperty valueProperty = (ValueProperty) currentProperty;
                        final OTSliderComponent slider = new OTSliderComponent(moduleButtonComponent, valueProperty::getPropertyValue,
                                valueProperty::getMaximumValue,
                                valueProperty::getMinimumValue,
                                valueProperty::setPropertyValue,
                                valueProperty.getPropertyLabel(), 85, propertyOffset, 0, 0);
                        moduleButtonComponent.addChild(slider);
                        propertyOffset += slider.getHeight();
                    }
                    if (currentProperty instanceof BooleanProperty) {
                        final BooleanProperty booleanProperty = (BooleanProperty) currentProperty;
                        final OTCheckboxComponent checkBox = new OTCheckboxComponent(moduleButtonComponent, booleanProperty.getPropertyLabel(), booleanProperty::getPropertyValue, booleanProperty::setPropertyValue,
                                85, propertyOffset, 0, 0);
                        moduleButtonComponent.addChild(checkBox);
                        propertyOffset += checkBox.getHeight();
                    }
                    if (currentProperty instanceof EnumProperty) {
                        EnumProperty enumProperty = (EnumProperty) currentProperty;
                        OTDropdownComponent dropdown = new OTDropdownComponent(moduleButtonComponent, enumProperty.getPropertyLabel(),
                                enumProperty::getEnumValues, enumProperty::getPropertyValue, enumProperty::increment, enumProperty::decrement,
                                85, propertyOffset, 0, 0);
                        moduleButtonComponent.addChild(dropdown);
                        propertyOffset += dropdown.getHeight();
                    }
                }
                tab.addChild(moduleButtonComponent);
            }
            rootComponent.addChild(tab);
            if (currentCategory.equals(ModuleCategory.COMBAT)) {
                tab.alphaTarget = 255;
                tab.alphaAnimation = 255;
                currentTabComponent = tab;
            }
            offset += rootComponent.getDefaultFontRenderer().getStringWidth(currentCategory.categoryName) + 20;
        }
        components.add(rootComponent);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        components.forEach(component -> component.onDrawScreen(mouseX, mouseY));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        components.forEach(component -> component.onMouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        components.forEach(component -> component.onMouseReleased(mouseX, mouseY, state));
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException {
        components.forEach(OTComponent::handleMouseInput);
        super.handleMouseInput();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
