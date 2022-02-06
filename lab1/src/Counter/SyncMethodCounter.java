package Counter;

public class SyncMethodCounter implements Counter {

    private int value = 0;

    public SyncMethodCounter() {}

    public int getValue() {
        return value;
    }

    @Override
    public synchronized void increment() {
        value += 1;
    }

    @Override
    public synchronized void decrement() {
        value -= 1;
    }

}
