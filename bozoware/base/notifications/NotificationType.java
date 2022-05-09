package bozoware.base.notifications;

import java.util.function.Function;

public enum NotificationType {
    INFO(time -> 0xFF4A68F9),
    WARNING(time -> 0xFFFF60 << 8),
    ERROR(time -> 0xFFFF << 16),
    SUCCESS(time -> 0xff00ff << 8);
    private final Function<Long, Integer> getColorFunc;

    NotificationType(Function<Long, Integer> getColorFunc) {
        this.getColorFunc = getColorFunc;
    }

    public int getColor(long time) {
        return this.getColorFunc.apply(time);
    }
}
