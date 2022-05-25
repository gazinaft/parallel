import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public class Program {
    public static void main(String[] args) {
//        var size = 1000;
//        var partitions = 2;
//        var a = MyMatrix.generate(size);
//        var b = MyMatrix.generate(size);
//        var pool = new ForkJoinPool();
//        var smpp = new FJStripeMultiplier(partitions, pool);
//        var smp = new SerialMultiplier();
//        var c = smp.mult(a, b);
//        var cp = smpp.mult(a, b);
//        c.print();
//        System.out.println();
//        cp.print();
//        System.out.println();
//        System.out.println(c.equals(cp));

        testCombined(1500, 8, 10); // q^2
    }

    static boolean testCorrectness() {
        var sizes = new int[] {4, 5, 100, 300};
        var threads = new int[] {2, 3, 10};
        for (var size: sizes) {
            for (var thread : threads) {
                var a = MyMatrix.generate(size);
                var b = MyMatrix.generate(size);
                var smpp = new StripeMultiplier(thread);
                var smp = new SerialMultiplier();
                var c = smp.mult(a, b);
                var cp = smpp.mult(a, b);
                if (!c.equals(cp)){
                    System.out.println("Wrong at size" + size + " and threadNumber " + thread);
                    return false;
                }
            }
        }
        return true;
    }


    static long testStripe(MyMatrix a, MyMatrix b, int threads) {
        var smpp = new StripeMultiplier(threads);
        System.gc();
        long start = System.nanoTime();
        var c = smpp.mult(a, b);
        long finish = System.nanoTime();
//        System.out.println("Stripe: " + (finish-start) + " nanoseconds at " + threads + " threads on size " + c.size);
        return finish - start;
    }

    static long testFJStripe(MyMatrix a, MyMatrix b, int threads, int partitions) {
        var smpp = new FJStripeMultiplier(threads, partitions);
        System.gc();
        long start = System.nanoTime();
        var c = smpp.mult(a, b);
        long finish = System.nanoTime();
//        System.out.println("FJStripe: " + (finish-start) + " nanoseconds at " + threads + " threads on size " + c.size);
        return finish - start;
    }

    static long testFox(MyMatrix a, MyMatrix b, int partitions) {
        var smpp = new FoxMultiplier(partitions);
        long start = System.nanoTime();
        var c = smpp.mult(a, b);
        long finish = System.nanoTime();
//        System.out.println("Fox: " + (finish-start) + " nanoseconds at " + partitions + " partitions on size " + c.size);
        return finish - start;
    }

    static long testSerial(MyMatrix a, MyMatrix b) {
        var smpp = new SerialMultiplier();
        long start = System.nanoTime();
        var c = smpp.mult(a, b);
        long finish = System.nanoTime();
//        System.out.println("Serial: " + (finish-start) + " nanoseconds on size " + c.size);
        return finish - start;
    }

    static void testCombined(int mSize, int threads, int partitions) {
        var res = new ArrayList<Double>();
        for (int i = 0; i < 10; ++i) {
            var a = MyMatrix.generate(mSize);
            var b = MyMatrix.generate(mSize);
            var results = new long[4];
            results[0] = testSerial(a, b);
            results[1] = testStripe(a, b, threads);
            results[2] = testFJStripe(a, b, threads, partitions);
//            results[3] = testFox(a, b, partitions);
//            System.out.println("Serial speedup: " + ((double) results[0] / (double) results[1]) + " at " + threads + " threads");
            var fjspeedup = (double) results[1] / (double) results[2];
            res.add(fjspeedup);
            System.out.println(fjspeedup);
//            System.out.println("ForkJoin speedup to stipe is: " + fjspeedup + " at " + threads + " threads");
//            System.out.println("Fox speedup: " + ((double) results[0] / (double) results[3]) + " at " + partitions + " partition");

            System.out.println();

        }
        System.out.println("Mean speedup of FJ Framework at " + partitions + "partitions");
        System.out.println((res.stream().reduce((acc, x) -> acc + x)).get() / (double) res.size());
    }

}
