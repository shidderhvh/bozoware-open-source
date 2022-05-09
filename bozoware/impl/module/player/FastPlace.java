package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.player.UpdatePositionEvent;


@ModuleData(moduleName = "Fast Place", moduleCategory = ModuleCategory.PLAYER)
public class FastPlace extends Module {


    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public FastPlace(){
        onUpdatePositionEvent = (e -> {
            mc.rightClickDelayTimer = 0;
        });
        onModuleDisabled = () -> {
            mc.rightClickDelayTimer = 6;
        };
    }
}
