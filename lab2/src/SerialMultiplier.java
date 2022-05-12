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


}
