package bozoware.impl.module.player;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.property.EnumProperty;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

@ModuleData(moduleName = "NoSlow", moduleCategory = ModuleCategory.PLAYER)
public class NoSlow extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    final EnumProperty<Modes> mode = new EnumProperty<>("Mode", Modes.NCP, this);

    public NoSlow() {
        setModuleSuffix(mode.getPropertyValue().toString());
        onUpdatePositionEvent = (updatePositionEvent -> {
            if (mode.getPropertyValue().equals(Modes.NCP)) {
                if (mc.thePlayer.isBlocking() && (mc.thePlayer.motionX != 0.0 || mc.thePlayer.motionZ != 0.0)) {
                    if (!updatePositionEvent.isPre()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    }
                }
            }
        });
        mode.onValueChange = () -> setModuleSuffix(mode.getPropertyValue().name());
    }

    public enum Modes {
        Vanilla,
        NCP
    }
}