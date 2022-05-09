package bozoware.visual.screens.dropdown.component;

import bozoware.base.BozoWare;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.util.visual.RenderUtil;
import bozoware.visual.font.MinecraftFontRenderer;
import bozoware.visual.screens.dropdown.component.sub.ModuleButtonComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;

public class ModuleCategoryFrame {

    private final ModuleCategory category;
    private boolean frameExpanded = true, isDraggingFrame;
    private final ArrayList<Component> childrenComponents = new ArrayList<>();
    private int x, y, distX, distY;
    private double motionAnimationX, motionAnimationY;
    public int frameWidth = 115, frameHeight = 14;
    MinecraftFontRenderer skeetIcons = BozoWare.getInstance().getFontManager().SkeetIcons;
    MinecraftFontRenderer arrowIcons = BozoWare.getInstance().getFontManager().ArrowIcons;
    MinecraftFontRenderer basicIcons = BozoWare.getInstance().getFontManager().BasicIcons;


    public ModuleCategoryFrame(ModuleCategory category, int x, int y){
        this.category = category;
        this.x = x;
        this.y = y;

        int buttonOffset = 14;
        for(Module module : BozoWare.getInstance().getModuleManager().getModulesByCategory(category)){
            addChildComponent(new ModuleButtonComponent(module, buttonOffset));
            buttonOffset += 14;
        }
    }

    public void onDrawScreen(int mouseX, int mouseY) {
        Gui.drawRectWithWidth(x - 1, y, frameWidth + 2, frameHeight, 0xff101010);

        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(category.categoryName, x + 3, y + 4, -1);
        switch(category.categoryName.toString()){
            case "Combat":
                basicIcons.drawStringWithShadow("a", x + 100, y + 5, -1);
                break;
            case "Player":
                basicIcons.drawStringWithShadow("c", x + 100, y + 5, -1);
                break;
            case "Movement":
                basicIcons.drawStringWithShadow("b", x + 100, y + 5, -1);
                break;
            case "Visual":
                basicIcons.drawStringWithShadow("g", x + 100, y + 5, -1);
                break;
            case "World":
                basicIcons.drawStringWithShadow("d", x + 100, y + 5, -1);
                break;
        }

        if(frameExpanded) {
            RenderUtil.drawRoundedRect(x - 1, y + 13, x - 1 + frameWidth + 2, y + 14 + getFrameHeight() + 1, 2, 0xff151515);
            RenderUtil.drawRoundedRect(x - 1, y + 13, x - 1 + frameWidth + 2, y + 14 + getFrameHeight() + 1, 2, 0xff101010);
        }
        else {
            RenderUtil.drawRoundedRect(x - 1, y + 13, x - 1 + frameWidth + 2, y + 14, 2, 0xff151515);
            RenderUtil.drawRoundedRect(x - 1, y + 13, x - 1 + frameWidth + 2, y + 14, 2, 0xff101010);
        }
        if(isDraggingFrame){
            motionAnimationX = RenderUtil.animate(mouseX - distX, motionAnimationX, 0.04);
            motionAnimationY = RenderUtil.animate(mouseY - distY, motionAnimationY, 0.04);
            x = (int) motionAnimationX;
            y = (int) motionAnimationY;
        }
        if(frameExpanded){
            childrenComponents.forEach(childComponent -> childComponent.onDrawScreen(mouseX, mouseY));
        }
    }

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {

        if(isHoveringFrame(mouseX, mouseY)){
            if(mouseButton == 0) {
                distX = mouseX - x;
                distY = mouseY - y;
                isDraggingFrame = true;
            }
            if(mouseButton == 1){
                frameExpanded = !frameExpanded;
                for (Component childrenComponent : childrenComponents) {
                    if(childrenComponent instanceof ModuleButtonComponent){
                        ((ModuleButtonComponent) childrenComponent).expanded = false;
                    }
                }
                childrenComponents.forEach(Component::onAnimationEvent);
            }
        }
        if(frameExpanded){
            childrenComponents.forEach(childComponent -> childComponent.onMouseClicked(mouseX, mouseY, mouseButton));

        }
    }

    public void onGuiClosed() {
        childrenComponents.forEach(Component::onGuiClosed);
    }

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {

        isDraggingFrame = false;

        if(frameExpanded){
            childrenComponents.forEach(childComponent -> childComponent.onMouseReleased(mouseX, mouseY, mouseButton));
        }
    }

    public void onKeyTyped(char typedKey) {
        if(frameExpanded){
            childrenComponents.forEach(childComponent -> childComponent.onKeyTyped(typedKey));
        }
    }

    public ArrayList<Component> getChildrenComponents() {
        return childrenComponents;
    }

    public void addChildComponent(Component component){
        component.setParentFrame(this);
        this.childrenComponents.add(component);
    }

    public void updateComponents(){
        int off = this.frameHeight;
        for(Component comp : childrenComponents) {
            comp.setOffset(off);
            off += comp.getHeight();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    private boolean isHoveringFrame(int mouseX, int mouseY){
        return mouseX >= x && mouseX <= x + frameWidth && mouseY >= y && mouseY <= y + frameHeight;
    }

    private int getFrameHeight(){
        int initialHeight = childrenComponents.size() * frameHeight;
        for(Component component : childrenComponents){
            if(component instanceof ModuleButtonComponent){
                if(((ModuleButtonComponent) component).expanded){
                    ArrayList<Component> visibleCheatSubs = new ArrayList<>(((ModuleButtonComponent) component).subComponents);
                    visibleCheatSubs.removeIf(Component::isHidden);
                    for (Component sub : visibleCheatSubs)
                        initialHeight += sub.getHeight();
                }
            }
        }
        return initialHeight;
    }

    public boolean isFrameExpanded(){
        return frameExpanded;
    }
}
