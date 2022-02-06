import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BallCanvas extends JPanel {
    private ArrayList<Ball> balls = new ArrayList<>();
    private ArrayList<Pocket> pockets;
    public JLabel label;
    private int goneCounter = 0;

    private void incrementGone(int count) {
        goneCounter += count;
        label.setText(goneCounter + " balls");
    }

    public void pauseAll() {
        for (Ball b : balls) {
            b.Pause();
        }
    }

    public void unpauseAll() {
        for (Ball b : balls) {
            b.UnPause();
        }
    }

    public ArrayList<Pocket> getPockets() {
        return pockets;
    }

    private ArrayList<Pocket> spawnPockets() {
        int w = getWidth();
        int h = getHeight();
        ArrayList<Pocket> res = new ArrayList<>();

        res.add(new Pocket(0, 0));
        res.add(new Pocket(w - Pocket.XSIZE, 0));
        res.add(new Pocket(0, h - Pocket.YSIZE));
        res.add(new Pocket(w - Pocket.XSIZE, h - Pocket.YSIZE));

        return res;
    }

    public void add(Ball b) {
        this.balls.add(b);
    }

    void paintPockets(Graphics2D g2) {
        for(int i = 0; i < pockets.size(); i++) {
            Pocket p = pockets.get(i);
            p.draw(g2);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        pockets = spawnPockets();
        paintPockets(g2);
        incrementGone((int)balls.stream().filter(Ball::isGone).count());
        balls.removeIf(Ball::isGone);
        for(int i = 0; i < balls.size(); i++){
            Ball b = balls.get(i);
            b.draw(g2);
        }
    }
}
