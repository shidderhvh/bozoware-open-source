package bozoware.base.notifications;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class NotificationManager {
    public static LinkedBlockingQueue<Notification> pendingNotifications = new LinkedBlockingQueue<>();
    public static Notification currentNotification = null;

    public static void show(Notification notification) {
        pendingNotifications.add(notification);
    }

    public static void update() {
        if (currentNotification != null && !currentNotification.isShown()) {
            currentNotification = null;
        }

        if (currentNotification == null && !pendingNotifications.isEmpty()) {
            currentNotification = pendingNotifications.poll();
            currentNotification.show();
        }

    }

    public static void render() {
        update();

        if (currentNotification != null)
            currentNotification.render();
    }
}