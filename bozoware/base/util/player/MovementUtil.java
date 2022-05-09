package bozoware.base.util.player;

import bozoware.impl.event.player.PlayerMoveEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import java.text.DecimalFormat;

public class MovementUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private static double speed, currentDistance, lastDistance;
    public static boolean prevOnGround;

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2875;
        if (mc.thePlayer != null)
            if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
                final int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
                baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
            }
        return baseSpeed;
    }
    public static double getBaseJumpMotion() {
        double baseJumpMotion = 0.42F;
        if (mc.thePlayer.isPotionActive(Potion.jump))
        {
            baseJumpMotion += (double)((float)(mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
        }
        return baseJumpMotion;
    }


    public static double getBaseMoveSpeed(double d) {
        double baseSpeed = d;
        if (mc.thePlayer != null)
            if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
                final int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
                baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
            }
        return baseSpeed;
    }

    public static void hClip(double offset) {
        mc.thePlayer.setPosition(mc.thePlayer.posX + -MathHelper.sin(getDirection()) * offset, mc.thePlayer.posY, mc.thePlayer.posZ + MathHelper.cos(getDirection()) * offset);
    }

    public static void setSpeed(PlayerMoveEvent e, double moveSpeed) {
        setSpeed(e, moveSpeed,
                mc.thePlayer.rotationYaw, mc.thePlayer.moveStrafing, mc.thePlayer.movementInput.moveForward);
    }

    public static void setSpeed(PlayerMoveEvent moveEvent, double moveSpeed, float pseudoYaw, double pseudoStrafe, double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;
        if (pseudoForward != 0) {
            if (pseudoStrafe > 0) {
                yaw = pseudoYaw + (pseudoForward > 0 ? -45 : 45);
            } else if (pseudoStrafe < 0) {
                yaw = pseudoYaw + (pseudoForward > 0 ? 45 : -45);
            }

            strafe = 0.0D;
            if (pseudoForward > 0) {
                forward = 1;
            } else if (pseudoForward < 0) {
                forward = -1;
            }
        }

        if (strafe > 0) {
            strafe = 1;
        } else if (strafe < 0) {
            strafe = -1;
        }

        double mx = Math.cos(Math.toRadians((yaw + 90)));
        double mz = Math.sin(Math.toRadians((yaw + 90)));
        moveEvent.setMotionX(forward * moveSpeed * mx + strafe * moveSpeed * mz);
        moveEvent.setMotionZ(forward * moveSpeed * mz - strafe * moveSpeed * mx);
    }

    public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    public static void TPSpeed(final double moveSpeed) {
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        if(mc.thePlayer.isMoving()){
            if(mc.thePlayer.getHorizontalFacing() == EnumFacing.EAST){
                mc.thePlayer.setPositionAndUpdate(x + moveSpeed, y, z);
            }
            if(mc.thePlayer.getHorizontalFacing() == EnumFacing.WEST){
                mc.thePlayer.setPositionAndUpdate(x - moveSpeed, y, z);
            }
            if(mc.thePlayer.getHorizontalFacing() == EnumFacing.NORTH){
                mc.thePlayer.setPositionAndUpdate(x, y, z - moveSpeed);
            }
            if(mc.thePlayer.getHorizontalFacing() == EnumFacing.SOUTH){
                mc.thePlayer.setPositionAndUpdate(x, y, z + moveSpeed);
            }
        }
    }

    public static boolean isMoving() {
        return mc.thePlayer.moveForward != 0 ||  mc.thePlayer.moveStrafing != 0;
    }

    public static double getMoveSpeed() {
        return Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static double lastDist() {
        if(mc.thePlayer != null) {
            double xDist = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double zDist = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            return Math.sqrt(xDist * xDist + zDist * zDist);
        }
        return 0;
    }

    public static void Dynamic(double Speed, float jumpHeight, double airFriction, double distanceDiff, boolean depresscito, boolean timerBoost, double timerSpeedGround, double timerSpeedAir) {
        if(mc.thePlayer.onGround && mc.thePlayer.isMoving()){
            if(timerBoost){
                mc.timer.timerSpeed = (float) timerSpeedGround;
            }
            mc.thePlayer.motionY = jumpHeight;
            speed = Math.max(getBaseMoveSpeed() * Speed, speed * Speed);
            prevOnGround = true;
        }

        if(mc.thePlayer.isAirBorne){
            if(timerBoost){
                mc.timer.timerSpeed = (float) timerSpeedAir;
            }
            if (prevOnGround) {
                speed -= distanceDiff * (speed - MovementUtil.getBaseMoveSpeed());
                prevOnGround = false;
            }
            else if(!depresscito){
                speed -= speed / airFriction;
            }
            else {
                speed -= speed / airFriction - 1.0E-9;
            }

            MovementUtil.setMoveSpeed(speed);
        }
    }

    public static void setMoveSpeed(final double moveSpeed) {
        float forward = MovementUtil.mc.thePlayer.movementInput.moveForward;
        float strafe = MovementUtil.mc.thePlayer.movementInput.moveStrafe;
        float yaw = MovementUtil.mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            MovementUtil.mc.thePlayer.motionX = 0.0;
            MovementUtil.mc.thePlayer.motionZ = 0.0;
        }
        int d = 45;
        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += ((forward > 0.0) ? -d : d);
            }
            else if (strafe < 0.0) {
                yaw += ((forward > 0.0) ? d : -d);
            }
            strafe = 0.0f;
            if (forward > 0.0) {
                forward = 1.0f;
            }
            else if (forward < 0.0) {
                forward = -1.0f;
            }
        }
        final double xDist = forward * moveSpeed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * moveSpeed * Math.sin(Math.toRadians(yaw + 90.0f));
        final double zDist = forward * moveSpeed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * moveSpeed * Math.cos(Math.toRadians(yaw + 90.0f));
        mc.thePlayer.motionX = xDist;
        mc.thePlayer.motionZ = zDist;
    }

    public static void setSpeed(double speed) {
        if (mc.thePlayer.isMoving()) {
            Minecraft.getMinecraft().thePlayer.motionX = (-MathHelper.sin(getDirection()) * speed);
            Minecraft.getMinecraft().thePlayer.motionZ = (MathHelper.cos(getDirection()) * speed);
        } else {
            Minecraft.getMinecraft().thePlayer.motionX = 0;
            Minecraft.getMinecraft().thePlayer.motionZ = 0;
        }
    }

    public static String getBPS() {
        double lastDist;
        double xDist = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
        double zDist = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
        lastDist = StrictMath.sqrt(xDist * xDist + zDist * zDist);
        double speed1 = lastDist * 20 * mc.timer.timerSpeed;
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(speed1);
    }

    public static float getDirection() {
        float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        float forward = Minecraft.getMinecraft().thePlayer.moveForward;
        float strafe = Minecraft.getMinecraft().thePlayer.moveStrafing;
        yaw += (forward < 0.0F ? 180 : 0);
        if (strafe < 0.0F) {
            yaw += (forward == 0.0F ? 90 : forward < 0.0F ? -45 : 45);
        }
        if (strafe > 0.0F) {
            yaw -= (forward == 0.0F ? 90 : forward < 0.0F ? -45 : 45);
        }

        return yaw * 0.017453292F;
    }

    public static float getDirectionStrafeFix(float forward, float strafing, float yaw)
    {
        if(forward == 0.0 && strafing == 0.0) return yaw;
        boolean reversed = (forward  < 0.0);
        double strafingYaw = 90.0 * ((forward > 0.0) ? 0.5 : (reversed ? -0.5 : 1.0));
        if(reversed) yaw += 180.0;
        if(strafing > 0){
            yaw -= strafingYaw;
        }else if(strafing < 0.0){
            yaw += strafingYaw;
        }
        return yaw;
    }

    public static long getBaseMoveSpeedLong() {
        double baseSpeed = 32;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            final int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed)
                    .getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return (long) (baseSpeed / 32);
    }

    public static float getBaseMoveSpeedFloat() {
        double baseSpeed = 0.6;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            final int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed)
                    .getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return (float)baseSpeed;
    }

    public static double getBaseSpeed() {
        return Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static void setMoveSpeed(long val) {
        float f = val * 0.1F;
        MovementUtil.setSpeed(f);
    }
    public static boolean setBaseMoveSpeed() {
        float f = (float) (MovementUtil.getBaseMoveSpeedFloat());
        {
            MovementUtil.setMoveSpeed(f / 2.45);
            return true;
        }
    }
}
