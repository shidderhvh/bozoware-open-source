package bozoware.impl.event.player;

import bozoware.base.event.Event;

public class PlayerStepEvent implements Event {
    private float stepHeight;
    private double heightStepped;
    private boolean pre;

    public PlayerStepEvent(final float stepHeight) {
        this.stepHeight = stepHeight;
        this.pre = true;
    }

    public float getStepHeight() {
        return stepHeight;
    }

    public void setStepHeight(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public double getHeightStepped() {
        return heightStepped;
    }

    public void setHeightStepped(double heightStepped) {
        this.heightStepped = heightStepped;
    }

    public boolean isPre() {
        return pre;
    }

    public void setPost() {
        this.pre = false;
    }

}
