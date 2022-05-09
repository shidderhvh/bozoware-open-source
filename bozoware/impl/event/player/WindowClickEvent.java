package bozoware.impl.event.player;

import bozoware.base.event.Event;

public class WindowClickEvent implements Event {
    private final int windowId;
    private final int slot;
    private final int hotbarSlot;
    private final int mode;

    public WindowClickEvent(final int windowId, final int slot, final int hotbarSlot, final int mode) {
        this.windowId = windowId;
        this.slot = slot;
        this.hotbarSlot = hotbarSlot;
        this.mode = mode;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public int getSlot() {
        return this.slot;
    }

    public int getHotbarSlot() {
        return this.hotbarSlot;
    }

    public int getMode() {
        return this.mode;
    }
}
