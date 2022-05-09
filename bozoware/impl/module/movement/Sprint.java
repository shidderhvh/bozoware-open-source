package bozoware.impl.module.movement;

import bozoware.base.BozoWare;
import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.impl.event.player.PlayerMoveEvent;
import bozoware.impl.module.player.BlockFly;
import bozoware.impl.property.BooleanProperty;

@ModuleData(moduleName = "Sprint", moduleCategory = ModuleCategory.MOVEMENT)
public class Sprint extends Module {

    private final BooleanProperty omniBool = new BooleanProperty("Omni-Sprint", true, this);

    @EventListener
    EventConsumer<PlayerMoveEvent> playerMoveEvent;

    public Sprint() {
//        setModuleSuffix("Vanilla");
        setModuleBind(0);
        onModuleEnabled = () -> {

        };
        playerMoveEvent = (updatePositionEvent -> {
            if(!(BozoWare.getInstance().getModuleManager().getModuleByClass.apply(BlockFly.class).isModuleToggled())) {
                if (!omniBool.getPropertyValue()) {
                    if (mc.thePlayer.isMovingForward() && (mc.thePlayer.getFoodStats().getFoodLevel() > 6 || mc.thePlayer.capabilities.isCreativeMode) && !mc.thePlayer.isCollidedHorizontally) {
                        mc.thePlayer.setSprinting(true);
                    }
                } else {
                    if (Wrapper.getPlayer().isMoving() && (Wrapper.getPlayer().getFoodStats().getFoodLevel() > 6 || Wrapper.getPlayer().capabilities.isCreativeMode) && !mc.thePlayer.isCollidedHorizontally) {
                        mc.thePlayer.setSprinting(true);
                    }
                }
            }
        });
    }

}
