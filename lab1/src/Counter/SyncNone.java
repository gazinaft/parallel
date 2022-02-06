package Counter;

public class SyncNone implements Runnable {

    private final Counter c;
    private final boolean toIncrement;

    SyncNone(Counter count, boolean inc) {
        c = count;
        toIncrement = inc;
    }

    private void increment() {
        for (int i = 0; i < 100_000; ++i) {
            c.increment();
        }
    }

    public void decrement() {
        for (int i = 0; i < 100_000; ++i) {
            c.decrement();
        }
    }

    @Override
    public void run() {
        if (toIncrement) increment();
        else decrement();
    }
}
