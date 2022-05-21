package ConsumerProducer;

import java.util.Random;

public class Producer implements Runnable {
    private Drop drop;
    private int ARRSIZE = 1000;

    public Producer(Drop drop) {
        this.drop = drop;
    }

    public void run() {
        Random random = new Random();
        int[] importantInfo = new int[ARRSIZE];

        drop.put(ARRSIZE);
        for (int i = 0; i < importantInfo.length; i++) {
            importantInfo[i] = i;
            drop.put(importantInfo[i]);
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {}
        }
    }
}