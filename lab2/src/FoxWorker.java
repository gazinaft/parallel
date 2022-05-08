import java.util.concurrent.ArrayBlockingQueue;

public class FoxWorker implements Runnable {

    private final int[][] a;
    ArrayBlockingQueue<int[][]> ABlocks;
    ArrayBlockingQueue<int[][]> BBlocks;

    ArrayBlockingQueue<int[][]>[] aRow;
    ArrayBlockingQueue<int[][]> nextB;

    private final int iterations;
    private int currentIteration;
    private final MyMatrix res;
    private final int startRow;
    private final int startColumn;

    private final int index;
    private final int jndex;


    public FoxWorker(int[][] a, int startRow, int startColumn, int index, int jndex, int iterations, MyMatrix result) {
        this.a = a;
        this.startRow = startRow;
        this.startColumn = startColumn;
        ABlocks = new ArrayBlockingQueue<>(3);
        BBlocks = new ArrayBlockingQueue<>(3);
        this.index = index;
        this.iterations = iterations;
        res = result;
        this.jndex = jndex;
    }

    @Override
    public void run() {
        while (currentIteration < iterations) {
            try {
                multiply();
            } catch (InterruptedException e) {
                System.out.println("error at thread " + startRow + "-" + startColumn);
                e.printStackTrace();
            }
            currentIteration++;
        }
    }

    synchronized void multiply() throws InterruptedException {
        if (jndex == (currentIteration + index) % iterations) {
            for (var q: aRow) {
                q.put(a);
            }
        }
        var as = ABlocks.take();
        var bs = BBlocks.take();

        for (int i = 0; i < as.length; i++) {
            for (int j = 0; j < bs[0].length; j++) {
                for (int k = 0; k < bs.length; k++) {
                    res.data[i + startRow][j + startColumn] += as[i][k] * bs[k][j];
                }
            }
        }
        nextB.put(bs);
    }
}
