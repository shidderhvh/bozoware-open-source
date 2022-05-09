package bozoware.visual.screens.dropdown.component;

import bozoware.base.BozoWare;
import bozoware.impl.module.visual.HUD;
import bozoware.visual.font.MinecraftFontRenderer;

public class Component {

    private ModuleCategoryFrame parentFrame;

    public final int componentAccentColor = HUD.getInstance().bozoColor;
    public final int componentAccentColorHovered = HUD.getInstance().bozoColor2;

    public final MinecraftFontRenderer getFontRenderer() {
        return BozoWare.getInstance().getFontManager().mediumFontRenderer;
    }

    public ModuleCategoryFrame getParentFrame() {
        return parentFrame;
    }

    public void setParentFrame(ModuleCategoryFrame frame){
        this.parentFrame = frame;
    }

    public void onDrawScreen(int mouseX, int mouseY) {

    }

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    public void onKeyTyped(int typedKey) {

    }

    public int getColor1(){
        return componentAccentColor;
    }

    public int getColor2(){
        return componentAccentColorHovered;
    }

    public double getHeight(){
        return 14;
    }

    public void onAnimationEvent(){

    }

    public boolean isHidden() {
        return false;
    }

    public void setOffset(int offset){

    }

    public void onGuiClosed() {

    }
}
