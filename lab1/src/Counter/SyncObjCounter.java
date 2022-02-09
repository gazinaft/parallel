package Counter;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SyncObjCounter implements Counter {

    private int value = 0;
    private final Lock lock;

    public SyncObjCounter() {
        lock = new ReentrantLock();
    }

    public int getValue() {
        return value;
    }

    @Override
    public void increment() {
        lock.lock();
        value += 1;
        lock.unlock();
    }

    @Override
    public void decrement() {
        lock.lock();
        value -= 1;
        lock.unlock();
    }

}
