package Counter;

public class SyncBlock implements Runnable {

    private final Counter c;
    private final boolean toIncrement;

    SyncBlock(Counter count, boolean inc) {
        c = count;
        toIncrement = inc;
    }

    private void increment() {
        for (int i = 0; i < 100_000; ++i) {
            synchronized (c) {
                c.increment();
            }
        }
    }

    public void decrement() {
        for (int i = 0; i < 100_000; ++i) {
            synchronized (c) {
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
