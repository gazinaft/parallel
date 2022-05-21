package ConsumerProducer;

import java.util.Random;

public class Consumer implements Runnable {
    private Drop drop;

    public Consumer(Drop drop) {
        this.drop = drop;
    }

    public void run() {
        Random random = new Random();
        int size = drop.take();
        for (int i = 0; i < size; ++i) {
            var message = drop.take();
            System.out.println("MESSAGE " + (i + 1) + " OF " + size + " RECEIVED: " + message);
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {}
        }
    }
}
