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

        Thread inc = new Thread(new NonSyncRunner(c, true));
        Thread dec = new Thread(new NonSyncRunner(c, false));

        experiment(c, inc, dec);
        long finish = System.nanoTime();
        System.out.println("Non synchronized");
        //        logTime(finish - start);
        System.out.println("====================================");

        // Sync methods
        start = System.nanoTime();
        Counter c2 = new SyncMethodCounter();

        Thread inc2 = new Thread(new NonSyncRunner(c2, true));
        Thread dec2 = new Thread(new NonSyncRunner(c2, false));

        experiment(c2, inc2, dec2);
        finish = System.nanoTime();
        System.out.println("Synchronized methods");
        logTime(finish - start);
        // Sync block
        start = System.nanoTime();
        Counter c3 = new SyncBlockCounter();

        Thread inc3 = new Thread(new NonSyncRunner(c3, true));
        Thread dec3 = new Thread(new NonSyncRunner(c3, false));

        experiment(c3, inc3, dec3);
        finish = System.nanoTime();
        System.out.println("Synchronized block");
        logTime(finish - start);

        // Sync counter object
        start = System.nanoTime();
        Counter c5 = new SyncObjCounter();

        Thread inc5 = new Thread(new NonSyncRunner(c5, true));
        Thread dec5 = new Thread(new NonSyncRunner(c5, false));

        experiment(c5, inc5, dec5);
        finish = System.nanoTime();
        System.out.println("Locked object");
        logTime(finish - start);


        System.out.println("Minimum time is " + minTime);
    }
}
