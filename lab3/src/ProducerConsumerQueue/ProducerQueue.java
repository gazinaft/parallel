package ProducerConsumerQueue;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class ProducerQueue implements Runnable {
    private ArrayBlockingQueue drop;
    private int ARRSIZE = 1000;

    public ProducerQueue(ArrayBlockingQueue queue) {
        this.drop = queue;
    }

    public void run() {
        Random random = new Random();
        int[] importantInfo = new int[ARRSIZE];
        try {
            drop.put(ARRSIZE);
        } catch (InterruptedException ex) {}
        for (int i = 0; i < importantInfo.length; i++) {
            importantInfo[i] = i;
            try {
                drop.put(importantInfo[i]);
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {}
        }
    }
}
