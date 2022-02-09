package Counter;

public class SyncBlockCounter implements Counter {

    private int value = 0;
    final Object lock;

    public SyncBlockCounter() {
        lock = new Object();
    }

    public int getValue() {
        return value;
    }

    @Override
    public void increment() {
        synchronized (lock) {
            value += 1;
        }
    }

    @Override
    public void decrement() {
        synchronized (lock) {
            value -= 1;
        }
    }

}
