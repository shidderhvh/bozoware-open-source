package bozoware.impl.module.movement;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.property.EnumProperty;

@ModuleData(moduleName = "High Jump", moduleCategory = ModuleCategory.MOVEMENT)
public class HighJump extends Module {

    private final EnumProperty mode = new EnumProperty("Mode", HighJumpEnum.VIPER, this);

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    public static boolean falling;
    public static boolean boosting;
    public double yEnable;

    public HighJump(){
        onModuleEnabled = () -> {
//            PlayerUtils.damageVerusAdvanced();
            boosting = true;
            falling = false;
            yEnable = mc.thePlayer.posY;
        };
        onUpdatePositionEvent = (e -> {
            if(mode.getPropertyValue().equals(HighJumpEnum.VIPER)) {
                e.setOnGround(true);
                if (boosting && !falling) {
                    mc.thePlayer.motionY = 2;
                }
                if (yEnable <= 10) {
                    this.toggleModule();
                }
                if(mc.thePlayer.onGround){
                    this.toggleModule();
                }
            }
        });
        onModuleDisabled = () -> {
            falling = false;
            boosting = true;
        };
    }
    private enum HighJumpEnum {
        VIPER("Viper");

        private final String name;

        HighJumpEnum(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
