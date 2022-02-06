package Counter;

public class CounterTask {
    static void experiment(Counter c, Thread inc, Thread dec) {
        inc.start();
        dec.start();
        try {
            inc.join();
            dec.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(c.getValue());
    }

    static long minTime = 0L;

    static void logTime(long time) {
        if (minTime == 0L) {
            minTime = time;
        } else {
            minTime = Long.min(minTime, time);
        }
        System.out.println("Time passed " + time + " nanoseconds");
        System.out.println("====================================");
    }

    public static void main(String[] args) {

        int warmup = 0;
        for(int i = 0; i < 100000; ++i) {
            warmup += Math.pow(2, i);
        }
        System.out.println("Warmup finished" + warmup);
        // non sync
        long start = System.nanoTime();
        Counter c = new NonSyncCounter();

        Thread inc = new Thread(new SyncNone(c, true));
        Thread dec = new Thread(new SyncNone(c, false));

        System.out.println("Non synchronized");
        experiment(c, inc, dec);
        long finish = System.nanoTime();
//        logTime(finish - start);

        // Sync methods
        start = System.nanoTime();
        Counter c2 = new SyncMethodCounter();

        Thread inc2 = new Thread(new SyncNone(c2, true));
        Thread dec2 = new Thread(new SyncNone(c2, false));

        System.out.println("Synchronized methods");
        experiment(c2, inc2, dec2);
        finish = System.nanoTime();
        logTime(finish - start);
        // Sync block
        start = System.nanoTime();
        Counter c3 = new NonSyncCounter();

        Thread inc3 = new Thread(new SyncBlock(c3, true));
        Thread dec3 = new Thread(new SyncBlock(c3, false));

        System.out.println("Synchronized block");
        experiment(c3, inc3, dec3);
        finish = System.nanoTime();
        logTime(finish - start);


        // Sync object lock not counter
        start = System.nanoTime();
        Counter c4 = new NonSyncCounter();

        Thread inc4 = new Thread(new SyncObj(c4, true));
        Thread dec4 = new Thread(new SyncObj(c4, false));
        System.out.println("Synchronized handler object");
        experiment(c4, inc4, dec4);
        finish = System.nanoTime();
        logTime(finish - start);


        // Sync counter object
        start = System.nanoTime();
        Counter c5 = new SyncObjCounter();

        Thread inc5 = new Thread(new SyncNone(c5, true));
        Thread dec5 = new Thread(new SyncNone(c5, false));

        System.out.println("Synchronized counter object");
        experiment(c5, inc5, dec5);
        finish = System.nanoTime();
        logTime(finish - start);


        System.out.println("Minimum time is " + minTime);
    }
}
