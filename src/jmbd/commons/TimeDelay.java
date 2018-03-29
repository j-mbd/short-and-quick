package jmbd.commons;

public class TimeDelay {

    /**
     * Pauses by busy-waiting.
     *
     * @param nanos
     */
    @SuppressWarnings("empty-statement")
    public void pauseNanos(long nanos) {

        long stopAt = System.nanoTime() + nanos;
        while (stopAt > System.nanoTime());
    }

    public void pauseMillis(long millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
        }
    }
}
