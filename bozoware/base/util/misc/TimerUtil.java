package bozoware.base.util.misc;

public final class TimerUtil
{
    private long currentMs;

    public TimerUtil() {
        this.reset();
    }

    public long lastReset() {
        return this.currentMs;
    }

    public boolean hasReached(final long milliseconds) {
        return this.elapsed() > milliseconds;
    }

    public long elapsed() {
        return System.currentTimeMillis() - this.currentMs;
    }

    public void reset() {
        this.currentMs = System.currentTimeMillis();
    }
    public void setTime(long time) {
        this.currentMs = time;
    }
}
