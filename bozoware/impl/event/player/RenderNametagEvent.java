package bozoware.impl.event.player;

import bozoware.base.event.CancellableEvent;
import bozoware.base.event.Event;
import net.minecraft.entity.EntityLivingBase;

public final class RenderNametagEvent extends CancellableEvent implements Event {
    private final EntityLivingBase entity;
    private String renderedName;

    public RenderNametagEvent(EntityLivingBase entity, String renderedName) {
        this.entity = entity;
        this.renderedName = renderedName;
    }

    public EntityLivingBase getEntity() {
        return this.entity;
    }

    public String getRenderedName() {
        return this.renderedName;
    }

    public void setRenderedName(String renderedName) {
        this.renderedName = renderedName;
    }
}
