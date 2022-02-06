package Counter;

public class SyncObjCounter implements Counter {

    private int value = 0;

    public SyncObjCounter() {}

    public int getValue() {
        return value;
    }

    @Override
    public void increment() {
        synchronized (this) {
            value += 1;
        }
    }

    @Override
    public void decrement() {
        synchronized (this) {
            value -= 1;
        }
    }

}
