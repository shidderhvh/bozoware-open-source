package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.player.UpdatePositionEvent;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;


@ModuleData(moduleName = "Speed Mine", moduleCategory = ModuleCategory.PLAYER)
public class Speedmine extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public Speedmine(){
        onModuleDisabled = () -> {
            mc.thePlayer.removePotionEffect(Potion.digSpeed.getId());
        };
        onUpdatePositionEvent = (e -> {
            if(e.isPre){
                mc.playerController.blockHitDelay = 0;
                boolean item = mc.thePlayer.getCurrentEquippedItem() == null;
                mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.getId(), 100, (int)(item ? 1 : 0)));
//                BozoWare.getInstance().chat(String.valueOf(mc.thePlayer.getActivePotionEffects()));
            }
//            else {
//                mc.thePlayer.removePotionEffect(Potion.digSpeed.getId());
//            }
        });
    }

}
