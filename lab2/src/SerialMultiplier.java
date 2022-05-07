public class SerialMultiplier implements IMultiplier {
    @Override
    public MyMatrix mult(MyMatrix a, MyMatrix b) {
        var res = new MyMatrix(a.size);
        for (int i = 0; i < a.size; i++) {
            for (int j = 0; j < a.size; j++) {
                for (int k = 0; k < a.size; k++) {
                    res.plusEquals(i, j, a.get(i, k) * b.get(k, j));
                }
            }
        }
        return res;
    }

    // stripe on stripe
    public static int[][] dataMult(int[][] a, int[][] b) {
        var res = new int[a.length][b[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                for (int k = 0; k < b[0].length; k++) {
                    res[i][j] += a[i][k] * b[j][k];
                }
            }
        }
        return res;
    }

    public static int vectorMult(int[] a, int[] b) {
        var res = 0;
        for (int i = 0; i < a.length; ++i) {
            res += a[i] * b[i];
        }

        return res;
    }
}
