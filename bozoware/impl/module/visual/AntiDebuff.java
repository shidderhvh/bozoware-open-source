package bozoware.impl.module.visual;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.player.UpdatePositionEvent;

@ModuleData(moduleName = "Zoot", moduleCategory = ModuleCategory.VISUAL)
public class AntiDebuff extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;


    public AntiDebuff(){
        onUpdatePositionEvent = (e -> {
//            if (mc.thePlayer.isPotionActive(Potion.blindness.getId())) {
//                mc.thePlayer.removePotionEffect(Potion.blindness.getId());
//            }
//            if (mc.thePlayer.isPotionActive(Potion.confusion.getId())) {
//                mc.thePlayer.removePotionEffect(Potion.confusion.getId());
//            }
//            if (mc.thePlayer.isPotionActive(Potion.digSlowdown.getId())) {
//                mc.thePlayer.removePotionEffect(Potion.digSlowdown.getId());
//            }
        });
    }

}
