package lk.jiat.techmart.util;

public final class TimerUtil {
    private TimerUtil() {
    }

    public static long start() {
        return System.nanoTime();
    }

    public static long elapsedMillis(long startNano) {
        return (System.nanoTime() - startNano) / 1_000_000;
    }
}
