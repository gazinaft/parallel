import java.util.concurrent.ArrayBlockingQueue;

public class FoxMultiplier implements IMultiplier {

    int sqrtParts;

    public FoxMultiplier(int sqrtParts) {
        this.sqrtParts = sqrtParts;
    }

    @Override
    public MyMatrix mult(MyMatrix a, MyMatrix b) {
        var res =  new MyMatrix(a.size);
        var partitions = sqrtParts * sqrtParts;

        FoxWorker[][] runs;
        try {
            runs = divideTasks(a, b, res);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Thread[] threads = new Thread[partitions];
        for (int i = 0; i < sqrtParts; ++i) {
            for (int j = 0; j < sqrtParts; ++j) {
                threads[i * sqrtParts + j] = new Thread(runs[i][j]);
            }
        }
        for (int i = 0; i < partitions; ++i) {
            threads[i].start();
        }
        try {
            for (int i = 0; i < partitions; ++i) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    public FoxWorker[][] divideTasks(MyMatrix a, MyMatrix b, MyMatrix resMatrix) throws InterruptedException {
        var partSize = a.size / sqrtParts;
        var res = new FoxWorker[sqrtParts][sqrtParts];
        for (int i = 0; i < sqrtParts; ++i) {
            for (int j = 0; j < sqrtParts; ++j) {
                var startRow = i*partSize;
                var startColumn = j*partSize;
                res[i][j] = new FoxWorker(a.getBlock(startRow, startColumn, partSize, partSize), startRow, startColumn, i, j, sqrtParts, resMatrix);
                res[i][j].BBlocks.put(b.getBlock(startRow, startColumn, partSize, partSize));
            }
        }
        for (int i = 0; i < sqrtParts; ++i) {
            ArrayBlockingQueue<int[][]>[] aRow;
            aRow = new ArrayBlockingQueue[sqrtParts];
            for (int j = 0; j < sqrtParts; ++j) {
                res[i][j].nextB = res[(sqrtParts + i - 1) % sqrtParts][j].BBlocks;
                aRow[j] = res[i][j].ABlocks;
                res[i][j].aRow = aRow;
            }
        }
        return res;
    }
}
