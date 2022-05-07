public class Program {
    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors());
        System.out.println(testCorrectness());
//        testSerial(1000);
//        testStripe(1000, 4);
//        testStripe(1000, 8);
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


    static long testStripe(int mSize, int threads) {
        var a = MyMatrix.generate(mSize);
        var b = MyMatrix.generate(mSize);
        var smpp = new StripeMultiplier(threads);
        long start = System.nanoTime();
        var c = smpp.mult(a, b);
        long finish = System.nanoTime();
        System.out.println("Stripe: " + (finish-start) + " nanoseconds at " + threads + " threads on size " + c.size);
        return finish - start;
    }

    static long testSerial(int mSize) {
        var a = MyMatrix.generate(mSize);
        var b = MyMatrix.generate(mSize);
        var smpp = new SerialMultiplier();
        long start = System.nanoTime();
        var c = smpp.mult(a, b);
        long finish = System.nanoTime();
        System.out.println("Serial: " + (finish-start) + " nanoseconds on size " + c.size);
        return finish - start;
    }
}
