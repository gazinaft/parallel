import java.util.concurrent.*;

public class FJStripeMultiplier implements IMultiplier {

    public final int stripeQuantity;
    public final ForkJoinPool pool;

    public FJStripeMultiplier(int threads, int partitions) {
        stripeQuantity = partitions;
        this.pool = new ForkJoinPool(threads);
    }

    @Override
    public MyMatrix mult(MyMatrix a, MyMatrix b) {
        var res =  new MyMatrix(a.size);


        StripeWorker[] runs = divideThreads(a, b, stripeQuantity, res);
        ForkJoinTask<Void>[] fs = new ForkJoinTask[runs.length];
        for (int i = 0; i < runs.length; i++) {
            StripeWorker rable = runs[i];
            fs[i] = (ForkJoinTask<Void>) pool.submit(rable);
        }
        for (var future : fs) {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return res;
    }

    StripeWorker[] divideThreads(MyMatrix a, MyMatrix b, int threadNumber, MyMatrix resMatrix) {
        int partitions = Math.min(a.size, threadNumber);
        int n = a.size / partitions;
        var res = new StripeWorker[partitions];
        var bts = b.transpose();
        for (int i = 0; i < partitions; ++i) {
            var start = i*n;
            var partSize = i == partitions - 1 ? Math.max(n, a.size - i * n) : n;
            var sr = new StripeWorker(start, a.getRows(start, partSize), partitions, resMatrix);
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
