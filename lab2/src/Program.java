public class Program {
    public static void main(String[] args) {
//        var size = 8;
//        var partitions = 2;
//        var a = MyMatrix.generate(size);
//        var b = MyMatrix.generate(size);
//        var smpp = new FoxMultiplier(partitions);
//        var smp = new SerialMultiplier();
//        var c = smp.mult(a, b);
//        var cp = smpp.mult(a, b);
//        c.print();
//        System.out.println();
//        cp.print();
//        System.out.println();
//        System.out.println(c.equals(cp));

        testCombined(1000, 8, 8);
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
        long start = System.nanoTime();
        var c = smpp.mult(a, b);
        long finish = System.nanoTime();
        System.out.println("Stripe: " + (finish-start) + " nanoseconds at " + threads + " threads on size " + c.size);
        return finish - start;
    }

    static long testFox(MyMatrix a, MyMatrix b, int partitions) {
        var smpp = new FoxMultiplier(partitions);
        long start = System.nanoTime();
        var c = smpp.mult(a, b);
        long finish = System.nanoTime();
        System.out.println("Fox: " + (finish-start) + " nanoseconds at " + partitions + " partitions on size " + c.size);
        return finish - start;
    }

    static long testSerial(MyMatrix a, MyMatrix b) {
        var smpp = new SerialMultiplier();
        long start = System.nanoTime();
        var c = smpp.mult(a, b);
        long finish = System.nanoTime();
        System.out.println("Serial: " + (finish-start) + " nanoseconds on size " + c.size);
        return finish - start;
    }

    static void testCombined(int mSize, int threads, int partitions) {
        for (int i = 0; i < 4; ++i) {
            var a = MyMatrix.generate(mSize);
            var b = MyMatrix.generate(mSize);
            var results = new long[3];
            results[0] = testSerial(a, b);
            results[1] = testStripe(a, b, threads);
            results[2] = testFox(a, b, partitions);
//            System.out.println("Serial speedup: " + ((double) results[0] / (double) results[1]) + " at " + threads + " threads");
//            System.out.println("Fox speedup: " + ((double) results[0] / (double) results[2]) + " at " + partitions + " partition");
            System.out.println();

        }
    }

}
