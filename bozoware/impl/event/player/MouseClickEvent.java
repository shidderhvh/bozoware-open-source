package bozoware.impl.event.player;

import bozoware.base.event.Event;

public class MouseClickEvent implements Event {
    private int button;

    public MouseClickEvent(final int button) {
        this.button = button;
    }

    public int getButton() {
        return this.button;
    }

    public void setButton(final int button) {
        this.button = button;
    }
}
