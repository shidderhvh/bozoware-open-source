package bozoware.impl.event.network;

import bozoware.base.event.CancellableEvent;
import net.minecraft.network.Packet;

public class PacketReceiveEvent extends CancellableEvent {

    private Packet<?> packet;

    public PacketReceiveEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}
