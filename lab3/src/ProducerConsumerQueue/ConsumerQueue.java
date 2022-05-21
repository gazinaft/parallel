package ProducerConsumerQueue;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class ConsumerQueue implements Runnable {
    private ArrayBlockingQueue<Integer> drop;

    public ConsumerQueue(ArrayBlockingQueue<Integer> drop) {
        this.drop = drop;
    }

    public void run() {
        Random random = new Random();
        int size = 0;
        try {
            size = drop.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (int i = 0; i < size; ++i) {
            try {
                var message = drop.take();
                System.out.println("MESSAGE " + (i + 1) + " OF " + size + " RECEIVED: " + message);
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {}
        }
    }
}
