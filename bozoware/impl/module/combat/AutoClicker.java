package bozoware.impl.module.combat;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.misc.TimerUtil;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.property.ValueProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Mouse;


@ModuleData(moduleName = "AutoClicker", moduleCategory = ModuleCategory.COMBAT)
public class AutoClicker extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final ValueProperty<Integer> cpsValue = new ValueProperty("CPS", 12, 1, 25, this);

    TimerUtil timer = new TimerUtil();

    public AutoClicker(){
        onUpdatePositionEvent = (e -> {
            if (Minecraft.getMinecraft().currentScreen == null && Mouse.isButtonDown(0)) {
                if (this.timer.hasReached(1000 / RandomUtils.nextInt((int)this.cpsValue.getPropertyValue() - 2, (int)this.cpsValue.getPropertyValue()))) {
                    KeyBinding.setKeyBindState(-100, true);
                    KeyBinding.onTick(-100);
                    this.timer.reset();
                }
                else {
                    KeyBinding.setKeyBindState(-100, false);
                }
            }
        });
    }

}
