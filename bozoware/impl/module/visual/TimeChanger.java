package bozoware.impl.module.visual;

import bozoware.base.event.EventConsumer;
import bozoware.base.event.EventListener;
import bozoware.base.module.Module;
import bozoware.base.module.ModuleCategory;
import bozoware.base.module.ModuleData;
import bozoware.base.util.Wrapper;
import bozoware.impl.event.network.PacketReceiveEvent;
import bozoware.impl.event.player.UpdatePositionEvent;
import bozoware.impl.event.world.WorldTimeChangeEvent;
import bozoware.impl.property.ValueProperty;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

@ModuleData(moduleName = "Time Changer", moduleCategory = ModuleCategory.VISUAL)
public class TimeChanger extends Module {

    @EventListener
    EventConsumer<WorldTimeChangeEvent> onWorldTimeChangeEvent;

    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final ValueProperty<Long> timeProperty = new ValueProperty<>("Hour", 20000L, 0L, 20000L, this);

    public TimeChanger() {
        onWorldTimeChangeEvent = (worldTimeChangeEvent -> worldTimeChangeEvent.setCancelled(true));
        onPacketReceiveEvent = (packetReceiveEvent -> {
           if (packetReceiveEvent.getPacket() instanceof S03PacketTimeUpdate)
               packetReceiveEvent.setCancelled(true);
        });
        onUpdatePositionEvent = (updatePositionEvent -> Wrapper.getWorld().setWorldTime(timeProperty.getPropertyValue()));
    }

}
