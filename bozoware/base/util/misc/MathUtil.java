package bozoware.base.util.misc;

import net.minecraft.client.Minecraft;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class MathUtil {

    public static double linearInterpolate(double min, double max, double norm) {return (max - min) * norm + min;}

    public static double roundToPlace(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public static float clampRotation() {
        float rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        float n = 1.0f;

        if (Minecraft.getMinecraft().thePlayer.movementInput.moveForward < 0.0f)
            rotationYaw += 180.0f;
        n = -0.5f;

        if (Minecraft.getMinecraft().thePlayer.movementInput.moveForward > 0.0f)
            n = 0.5f;

        if (Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe > 0.0f)
            rotationYaw -= 90.0f * n;

        if (Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe < 0.0f)
            rotationYaw += 90.0f * n;

        return rotationYaw * 0.017453292f;
    }
    public static double getRandomInRange(double min, double max) {
        Random random = new Random();
        double range = max - min;
        double scaled = random.nextDouble() * range;
        if (scaled > max) {
            scaled = max;
        }
        double shifted = scaled + min;

        if (shifted > max) {
            shifted = max;
        }
        return shifted;
    }
    public static float[] FindGCD(float x, float y){
        if(isZero(y))
            return new float[]{x, 0};
        else
            y = x % y;

        return new float[]{x, y};
    }
    public static boolean isZero(float y) {
        return y == 0;
    }
}