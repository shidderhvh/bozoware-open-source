package bozoware.impl.module.world;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.player.UpdatePositionEvent;

@ModuleData(moduleName = "Lightning Detector", moduleCategory = ModuleCategory.WORLD)
public class LightningDetector extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public LightningDetector(){
        onUpdatePositionEvent = (e -> {

        });
    }

}
