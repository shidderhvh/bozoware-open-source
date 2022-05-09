package bozoware.impl.event.visual;

import bozoware.base.event.CancellableEvent;
import net.minecraft.client.gui.ScaledResolution;

public class Render2DEvent extends CancellableEvent {

    private final ScaledResolution scaledResolution;

    public Render2DEvent(ScaledResolution scaledResolution) {
        this.scaledResolution = scaledResolution;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }
}
