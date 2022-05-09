package bozoware.impl.module.visual;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.visual.Render2DEvent;
import bozoware.visual.font.MinecraftFontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

@ModuleData(moduleName = "TabGUI", moduleCategory = ModuleCategory.VISUAL)
public class TabGUI extends Module {

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    public static int moduleNum = 1;
    public TabGUI(){

        onModuleEnabled = () -> {
            moduleNum = 1;
        };

        onRender2DEvent = (e -> {
            MinecraftFontRenderer MFR = BozoWare.getInstance().getFontManager().mediumFontRenderer;
            if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
                moduleNum = moduleNum + 1;
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_UP) && moduleNum != 1){
                moduleNum = moduleNum - 1;
            }
            Gui.drawRect(5, 18, 60, 80, 0x99000000);
                Gui.drawRect(5, moduleNum * 18, 60, moduleNum * 30, HUD.getInstance().bozoColor2);
            MFR.drawStringWithShadow("COMBAT", 8, 21, -1);
        });
    }
}
