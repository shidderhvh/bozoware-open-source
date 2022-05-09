package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.player.MovementUtil;
import bozoware.impl.event.player.UpdatePositionEvent;

@ModuleData(moduleName = "ResetVL", moduleCategory = ModuleCategory.PLAYER)
public class ResetVL extends Module {

    int jumps = 0;

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public ResetVL(){
        onUpdatePositionEvent = (e -> {
            MovementUtil.setSpeed(0);
            mc.thePlayer.posY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
            mc.thePlayer.lastTickPosY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
            mc.timer.timerSpeed = 2f;
            if(mc.thePlayer.onGround){
                jumps++;
                mc.thePlayer.jump();
            }
            if(jumps == 5){
                mc.timer.timerSpeed = 1;
                jumps = 0;
                this.toggleModule();
            }
        });
    }

}
