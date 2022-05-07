public class StripeMultiplier implements IMultiplier {

    public final int threadNumber;
    public StripeMultiplier(int threads) {
        threadNumber = threads;
    }

    @Override
    public MyMatrix mult(MyMatrix a, MyMatrix b) {
        var res =  new MyMatrix(a.size);

        StripeRunnable[] runs = divideThreads(a, b, threadNumber, res);
        Thread[] threads = new Thread[runs.length];
        for (int i = 0; i < runs.length; ++i) {
            threads[i] = new Thread(runs[i]);
        }
        for (int i = 0; i < runs.length; ++i) {
            threads[i].start();
        }
        try {
            for (int i = 0; i < runs.length; ++i) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    StripeRunnable[] divideThreads(MyMatrix a, MyMatrix b, int threadNumber, MyMatrix resMatrix) {
        int partitions = Math.min(a.size, threadNumber);
        int n = a.size / partitions;
        var res = new StripeRunnable[partitions];
        var bts = b.transpose();
        for (int i = 0; i < partitions; ++i) {
            var start = i*n;
            var partSize = i == partitions - 1 ? Math.max(n, a.size - i * n) : n;
            var sr = new StripeRunnable(start, a.getRows(start, partSize), partitions, resMatrix);
            sr.BStripes.add(bts.getRows(start, partSize));
            sr.BStarts.add(start);
            res[i] = sr;
        }
        for (int i = 0; i < partitions; ++i) {
                res[i].next = res[((i + 1) % partitions)];
        }
        return res;
    }

}
