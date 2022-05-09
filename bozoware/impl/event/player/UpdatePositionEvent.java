package bozoware.impl.event.player;

import bozoware.base.event.CancellableEvent;
import bozoware.base.util.Wrapper;

public class UpdatePositionEvent extends CancellableEvent {

    public double x, y, z;
    public float yaw, pitch;
    public boolean onGround;

    public boolean isPre;

    public UpdatePositionEvent(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.isPre = true;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;

    }

    public void setPre(boolean pre) {
        isPre = pre;
    }

    public boolean isPre() {
        return isPre;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        Wrapper.getPlayer().renderYawOffset = yaw;
        Wrapper.getPlayer().rotationYawHead = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
