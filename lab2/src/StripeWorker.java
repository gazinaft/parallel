import java.util.concurrent.ArrayBlockingQueue;

public class StripeWorker implements Runnable {

    public int[][] AStripes;
    private final int startRow;
    public final ArrayBlockingQueue<int[][]> BStripes;
    public final ArrayBlockingQueue<Integer> BStarts;
    public StripeWorker next;
    private final MyMatrix res;
    private int iteration;
    private final int partitions;

    public StripeWorker(int ARow, int[][] stripe, int partitions, MyMatrix result) {
        startRow = ARow;
        AStripes = stripe;
        this.partitions = partitions;
        BStripes = new ArrayBlockingQueue<>(30);
        BStarts = new ArrayBlockingQueue<>(30);
        res = result;
        iteration = 0;
    }

    @Override
    public void run() {
        while (iteration < partitions) {
            try {
                multiply();
            }catch (InterruptedException ex) {
                System.out.println("error at thread " + startRow);
                ex.printStackTrace();
            }
            iteration++;
        }
    }

    synchronized void multiply() throws InterruptedException {
        var bs = BStripes.take();
        int bStart = BStarts.take();
        for (int i = 0; i < AStripes.length; i++) {
            for (int j = 0; j < bs.length; j++) {
                for (int k = 0; k < bs[0].length; k++) {
                    res.data[i + startRow][j + bStart] += AStripes[i][k] * bs[j][k];
                }
            }
        }
        next.BStripes.put(bs);
        next.BStarts.put(bStart);

    }

}
