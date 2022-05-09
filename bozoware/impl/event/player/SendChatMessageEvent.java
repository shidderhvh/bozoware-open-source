package bozoware.impl.event.player;

import bozoware.base.event.CancellableEvent;

public class SendChatMessageEvent extends CancellableEvent {

    private String message;

    public SendChatMessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
