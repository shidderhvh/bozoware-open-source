package bozoware.impl.module.visual;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.UI.FlappyBirdGUI;
import bozoware.impl.event.visual.Render2DEvent;
import bozoware.impl.property.EnumProperty;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

@ModuleData(moduleName = "Games", moduleCategory = ModuleCategory.VISUAL)
public class Games extends Module {

    private final EnumProperty<gameMode> gameModeEnumProperty = new EnumProperty<>("Game Mode", gameMode.flappyBird, this);

    @EventListener
    EventConsumer<Render2DEvent> render2dEvent;

    public Games(){
        render2dEvent = (e -> {
            ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        switch(gameModeEnumProperty.getPropertyValue()){
            case flappyBird:
                mc.displayGuiScreen(new FlappyBirdGUI());
                break;
            }
        });
    }


   public enum gameMode{
        flappyBird,
       snake
   }
}
