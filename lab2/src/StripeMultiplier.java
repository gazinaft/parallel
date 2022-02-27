public class StripeMultiplier implements IMultiplier {
    @Override
    public MyMatrix mult(MyMatrix a, MyMatrix b) {
        return new MyMatrix(a.size);
    }
}
