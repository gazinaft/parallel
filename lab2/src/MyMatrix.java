import java.util.Random;

public class MyMatrix {
    private int[][] data;
    public final int size;
    public MyMatrix(int size) {
        data = new int[size][size];
        this.size = size;
    }

    public void set(int x, int y, int value) {
        data[x][y] = value;
    }

    public void plusEquals(int x, int y, int value) {
        data[x][y] = value;
    }

    public int[] getRow(int x) {
        return data[x];
    }

    public int get(int x, int y) {
        return data[x][y];
    }

    public static MyMatrix generate(int size) {
        var r = new Random();
        var m = new MyMatrix(size);
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                int multiplier = r.nextBoolean() ? 1 : -1;
                m.data[i][j] = r.nextInt() * multiplier;
            }
        }
        return m;
    }
}
