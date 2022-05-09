package bozoware.base.util.visual;

import bozoware.base.util.misc.MathUtil;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

public class ColorUtil {

    public static Color getHealthColorFromEntity(EntityLivingBase entity) {
        final double current = entity.getHealth();
        final double max = entity.getMaxHealth();
        final double percent = current/max;
        final Color c1 = Color.RED.darker();
        final Color c2 = Color.GREEN;
        return new Color((int) MathUtil.linearInterpolate(c1.getRed(), c2.getRed(), percent),
                (int) MathUtil.linearInterpolate(c1.getGreen(), c2.getGreen(), percent),
                (int) MathUtil.linearInterpolate(c1.getBlue(), c2.getBlue(), percent));
    }

    public static Color getHealthColorFromEntity(EntityLivingBase entity, Color c1, Color c2) {
        final double current = entity.getHealth();
        final double max = entity.getMaxHealth();
        final double percent = current/max;
        return new Color((int) MathUtil.linearInterpolate(c1.getRed(), c2.getRed(), percent),
                (int) MathUtil.linearInterpolate(c1.getGreen(), c2.getGreen(), percent),
                (int) MathUtil.linearInterpolate(c1.getBlue(), c2.getBlue(), percent));
    }

    public static Color interpolateColors(Color color1, Color color2, float point) {
        if (point > 1)
            point = 1;
        return new Color((int) ((color2.getRed() - color1.getRed()) * point + color1.getRed()),
                (int) ((color2.getGreen() - color1.getGreen()) * point + color1.getGreen()),
                (int) ((color2.getBlue() - color1.getBlue()) * point + color1.getBlue()));
    }

    public static Color interpolateColorsDynamic(int speed, int index, Color start, Color end) {
        int angle = (int) (((System.currentTimeMillis()) / speed + index) % 360);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return interpolateColors(start, end, angle / 360f);
    }
}
