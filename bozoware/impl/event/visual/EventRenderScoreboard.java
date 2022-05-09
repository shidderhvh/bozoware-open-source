package bozoware.impl.event.visual;

import bozoware.base.event.CancellableEvent;

public class EventRenderScoreboard extends CancellableEvent {
    private final float width;
    private final float height;
    private float positionY;
    private float positionX;
    private boolean background;

    public EventRenderScoreboard(float positionX, float positionY, float width, float height, int blurAmount) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.width = width;
        this.height = height;
        this.background = true;
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean getDrawBackground() {
        return background;
    }

    public void setBackground(boolean background) {
        this.background = background;
    }
}