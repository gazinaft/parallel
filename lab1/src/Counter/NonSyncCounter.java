package Counter;

public class NonSyncCounter implements Counter {

    private int value = 0;

    public NonSyncCounter() {}

    public int getValue() {
        return value;
    }

    @Override
    public void increment() {
        value += 1;
    }

    @Override
    public void decrement() {
        value -= 1;
    }

}
