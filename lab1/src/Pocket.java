import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Pocket {
    private static final Color color = Color.BLACK;
    public static final int XSIZE = 30;
    public static final int YSIZE = 30;
    public final int x;
    public final int y;

    Pocket(int x, int y) {
        this.x = x;
        this.y = y;
    }

    void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.fill(new Ellipse2D.Double(x, y, XSIZE, YSIZE));
    }

    public boolean collidesWith(int x2, int y2) {
        double collideRadius = (XSIZE + YSIZE) / 2.0;
        double distance = Math.sqrt(Math.pow(x - x2, 2) + Math.pow(y - y2, 2));
        return distance < collideRadius;
    }

    @Override
    public String toString() {
        return "Pocket{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
