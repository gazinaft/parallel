package ProducerConsumerQueue;

import java.util.concurrent.ArrayBlockingQueue;

public class ProducerConsumerQueueExample {
    public static void main(String[] args) {
        ArrayBlockingQueue<Integer> drop = new ArrayBlockingQueue<Integer>(1);
        (new Thread(new ProducerQueue(drop))).start();
        (new Thread(new ConsumerQueue(drop))).start();
    }
}