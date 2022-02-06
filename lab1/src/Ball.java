import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;

class Ball {
    private Component canvas;
    private static final int XSIZE = 20;
    private static final int YSIZE = 20;
    private Color color = Color.darkGray;
    private int x = 0;
    private int y = 0;
    private int dx = 2;
    private int dy = 2;
    private boolean isGone = false;
    private boolean isPaused = false;
    private final ArrayList<Pocket> pockets;

    public Ball(Component c, ArrayList<Pocket> ps){
        this.canvas = c;
        this.pockets = ps;
        if(Math.random() < 0.5){
            x = new Random().nextInt(this.canvas.getWidth());
            y = 0;
        } else {
            x = 0;
            y = new Random().nextInt(this.canvas.getHeight());
        }
    }

    public Color getColor() {
        return color;
    }

    public Ball(Component c, ArrayList<Pocket> ps, Color ballColor) {
        this(c, ps);
        this.color = ballColor;
    }

    // Non-random spawn
    public Ball(Component c, ArrayList<Pocket> ps, Color ballColor, int posX, int posY) {
        this.color = ballColor;
        this.canvas = c;
        this.pockets = ps;
        x = posX;
        y = posY;
        dy = 0;
    }

    public boolean isGone() {
        return isGone;
    }

    public void draw (Graphics2D g2){
        g2.setColor(color);
        g2.fill(new Ellipse2D.Double(x, y, XSIZE, YSIZE));
    }

    private boolean collidesWithPockets() {
        return pockets.stream().anyMatch((pocket) -> pocket.collidesWith(x, y));
    }

    private void collidesWithBorders() {
        if(x < 0) {
            x = 0;
            dx = -dx;
        }
        if(x + XSIZE >= this.canvas.getWidth()) {
            x = this.canvas.getWidth() - XSIZE;
            dx = -dx;
        }
        if(y < 0) {
            y = 0;
            dy = -dy;
        }
        if(y + YSIZE >= this.canvas.getHeight()) {
            y = this.canvas.getHeight() - YSIZE;
            dy = -dy;
        }
    }

    public void move(){
        if (isPaused) return;
        x += dx;
        y += dy;
        if (collidesWithPockets()) {
            isGone = true;
            this.canvas.repaint();
            return;
        }
        collidesWithBorders();
        this.canvas.repaint();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void Pause() {
        isPaused = true;
    }

    public void UnPause() {
        isPaused = false;
    }
}
