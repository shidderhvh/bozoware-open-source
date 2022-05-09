package bozoware.base.util.misc;

public class SessionUtil {

    static int bruh = (int) System.currentTimeMillis();

    public static long getCurrentTime() {
        return System.currentTimeMillis()- bruh;
    }

    public void reset() {
        bruh = (int) System.currentTimeMillis();
    }

}
