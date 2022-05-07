import java.util.Random;

public class MyMatrix {
    public int[][] data;
    public final int size;
    public MyMatrix(int size) {
        data = new int[size][size];
        this.size = size;
    }

    public void set(int x, int y, int value) {
        data[x][y] = value;
    }

    public void plusEquals(int x, int y, int value) {
        data[x][y] += value;
    }

    public int[] getRow(int x) {
        return data[x];
    }

    public int[][] getRows(int start, int len) {
        var res = new int[len][size];
        System.arraycopy(data, start, res, 0, len);
        return res;
    }

    public int get(int x, int y) {
        return data[x][y];
    }

    public void print() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                System.out.print(data[i][j] + "|");
            }
            System.out.println();
        }
    }

    public boolean equals(MyMatrix other) {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (other.get(i, j) != data[i][j]) return false;
            }
        }
        return true;
    }

    public MyMatrix transpose() {
        var res = new MyMatrix(size);
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                res.set(i, j, data[j][i]);
            }
        }
        return res;
    }

    public static MyMatrix generate(int size) {
        var r = new Random();
        var m = new MyMatrix(size);
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                int multiplier = r.nextBoolean() ? 1 : -1;
                m.data[i][j] = r.nextInt(5000) * multiplier;
            }
        }
        return m;
    }
}
