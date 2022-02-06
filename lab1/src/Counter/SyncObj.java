package Counter;

public class SyncObj implements Runnable {

    private final Counter c;
    private final boolean toIncrement;

    SyncObj(Counter count, boolean inc) {
        c = count;
        toIncrement = inc;
    }

    private void increment() {
        for (int i = 0; i < 100_000; ++i) {
            synchronized (this) {
                c.increment();
            }
        }
    }

    public void decrement() {
        for (int i = 0; i < 100_000; ++i) {
            synchronized (this) {
                c.decrement();
            }
        }
    }

    @Override
    public void run() {
        if (toIncrement) increment();
        else decrement();
    }
}
