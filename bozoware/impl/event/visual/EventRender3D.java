package bozoware.impl.event.visual;

import bozoware.base.event.CancellableEvent;

public class EventRender3D extends CancellableEvent {
    public float partialTicks;
    public EventRender3D(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
