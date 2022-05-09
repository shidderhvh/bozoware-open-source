package bozoware.visual.screens.dropdown;

import bozoware.base.module.ModuleCategory;
import bozoware.base.util.visual.BlurUtil;
import bozoware.base.util.visual.RenderUtil;
import bozoware.impl.module.visual.ClickGUI;
import bozoware.impl.module.visual.HUD;
import bozoware.visual.screens.dropdown.component.Component;
import bozoware.visual.screens.dropdown.component.ModuleCategoryFrame;
import bozoware.visual.screens.dropdown.component.sub.ModuleButtonComponent;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;

public class GuiDropDown extends GuiScreen {

    private static final ArrayList<ModuleCategoryFrame> MODULE_CATEGORY_FRAMES = new ArrayList<>();
    private double nekoAnimation;
    private int imageWidth, imageHeight, imageX;

    public static Runnable onStartTask = () -> {
        for(int i = 0; i < ModuleCategory.values().length; i++){
            MODULE_CATEGORY_FRAMES.add(new ModuleCategoryFrame(ModuleCategory.values()[i], 10 + i * 125, 10));
        }
    };

    @Override
    public void initGui() {
        MODULE_CATEGORY_FRAMES.forEach(moduleCategoryFrame -> {
            for(Component component : moduleCategoryFrame.getChildrenComponents()){
                if(component instanceof ModuleButtonComponent){
                    ((ModuleButtonComponent) component).subComponents.forEach(Component::onAnimationEvent);
                }
            }
        });
        nekoAnimation = height;
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        MODULE_CATEGORY_FRAMES.forEach(ModuleCategoryFrame::onGuiClosed);
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawGradientRect(0, 0, width, height, 0x60ffffff, HUD.getInstance().bozoColor);
        Gui.drawRectWithWidth(0, 0, width, height, 0x70000000);
        GL11.glPushMatrix();
        BlurUtil.blurArea(0, 0, width, height);
//        if(ClickGUI.getInstance().isModuleToggled()) {
            switch (ClickGUI.getInstance().loliMode.getPropertyValue()) {
                case Uzaki:
                    mc.getTextureManager().bindTexture(new ResourceLocation("BozoWare/Uzaki.png"));
                    imageWidth = 250;
                    imageHeight = 330;
                    imageX = 250;
                    break;
                case ZeroTwo:
                    mc.getTextureManager().bindTexture(new ResourceLocation("BozoWare/ZeroTwo.png"));
                    imageWidth = 300;
                    imageHeight = 330;
                    imageX = 300;
                    break;
                case Rias:
                    mc.getTextureManager().bindTexture(new ResourceLocation("BozoWare/anth.png"));
                    imageWidth = 225;
                    imageHeight = 330;
                    imageX = 225;
                    break;
                case Kanna:
                    mc.getTextureManager().bindTexture(new ResourceLocation("BozoWare/Kanna.png"));
                    imageWidth = 325;
                    imageHeight = 330;
                    imageX = 325;
                    break;
                case None:

                    break;
                default:
                    mc.getTextureManager().bindTexture(new ResourceLocation("BozoWare/Uzaki.png"));
                    imageWidth = 250;
                    imageHeight = 330;
                    break;
            }
//        }
        GL11.glColor4f(1, 1, 1, 1);
        nekoAnimation = RenderUtil.animate(height - 330, nekoAnimation, 0.03);
        if(!ClickGUI.getInstance().loliMode.getPropertyValue().equals(ClickGUI.loliModes.None))
        Gui.drawModalRectWithCustomSizedTexture(width - imageX, Math.round((float) nekoAnimation), 0, 0, 250, 330, imageWidth, imageHeight);
        GL11.glPopMatrix();
        MODULE_CATEGORY_FRAMES.forEach(moduleCategoryFrame -> {
            moduleCategoryFrame.onDrawScreen(mouseX, mouseY);
            moduleCategoryFrame.updateComponents();
        });
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        MODULE_CATEGORY_FRAMES.forEach(moduleCategoryFrame -> moduleCategoryFrame.onMouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        MODULE_CATEGORY_FRAMES.forEach(moduleCategoryFrame -> moduleCategoryFrame.onMouseReleased(mouseX, mouseY, state));
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_RSHIFT){
            mc.thePlayer.closeScreen();
        }
        MODULE_CATEGORY_FRAMES.forEach(moduleCategoryFrame -> moduleCategoryFrame.onKeyTyped(typedChar));
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
