package bozoware.impl.event.network;

import bozoware.base.event.CancellableEvent;
import net.minecraft.network.Packet;

public class PacketSendEvent extends CancellableEvent {

    private Packet<?> packet;

    public PacketSendEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}
