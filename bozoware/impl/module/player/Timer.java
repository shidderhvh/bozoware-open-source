package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.property.BooleanProperty;
import bozoware.impl.property.ValueProperty;

@ModuleData(moduleName = "Timer", moduleCategory = ModuleCategory.PLAYER)
public class Timer extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final ValueProperty<Float> speed = new ValueProperty<>("Timer Speed", 2F, 0.1F, 10F, this);
    private final BooleanProperty tickBool = new BooleanProperty("Tick", true, this);

    public Timer(){
        onUpdatePositionEvent = (e -> {
        if(!tickBool.getPropertyValue()) {
            mc.timer.timerSpeed = speed.getPropertyValue();
        } else {
            if(mc.thePlayer.ticksExisted % 2 == 0){
                mc.timer.timerSpeed = 1;
            } else {
                mc.timer.timerSpeed = speed.getPropertyValue();

            }
        }
        });
        onModuleDisabled = () -> {
            mc.timer.timerSpeed = 1;
        };
    }
    }
