import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BounceFrame extends JFrame {
    private BallCanvas canvas;
    public static final int WIDTH = 750;
    public static final int HEIGHT = 550;
    public JLabel label;

    public BounceFrame() {
        this.setSize(WIDTH, HEIGHT);
        this.setTitle("Bounce programm");
        this.canvas = new BallCanvas();
        System.out.println("In Frame Thread name = "
                + Thread.currentThread().getName());

        Container content = this.getContentPane();
        content.add(this.canvas, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.lightGray);
        JButton buttonStart = new JButton("Blue Ball");
        JButton buttonStop = new JButton("Stop");
        JButton buttonRedBall = new JButton("Red Ball");
        JButton buttonRace = new JButton("Red Blue Ball race");
        JButton buttonJoin = new JButton("Join Button");
        label = new JLabel("0 balls");
        canvas.label = label;
        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Ball b = new Ball(canvas, canvas.getPockets(), Color.BLUE);
                canvas.add(b);
                BallThread thread = new BallThread(b, 6);
                thread.start();
                System.out.println("Thread name = " +
                        thread.getName());
            }
        });

        buttonRedBall.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Ball red = new Ball(canvas, canvas.getPockets(), Color.RED);
                canvas.add(red);
                BallThread thread = new BallThread(red, 3);
                thread.start();
                System.out.println("Thread name Red Ball = " +
                        thread.getName());
            }
        });

        buttonRace.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int blueBallsCount = 100;

                ArrayList<BallThread> threads = new ArrayList<>();

                for (int i = 0; i < blueBallsCount; ++i) {
                    Ball blue = new Ball(canvas, canvas.getPockets(), Color.BLUE, canvas.getWidth() / 2, canvas.getHeight() / 2);
                    canvas.add(blue);
                    BallThread td = new BallThread(blue, 6);
                    threads.add(td);
                }
                Ball red = new Ball(canvas, canvas.getPockets(), Color.RED, canvas.getWidth() / 2, canvas.getHeight() / 2);
                BallThread thread = new BallThread(red, 1);
                threads.add(thread);
                canvas.add(red);

                for (BallThread bt: threads) {
                    bt.start();
                }
            }
        });

        buttonJoin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.pauseAll();
                Ball orange = new Ball(canvas, canvas.getPockets(), Color.ORANGE);
                canvas.add(orange);
                BallThread thread = new BallThread(orange, 1);
                thread.start();
                System.out.println("Thread name Blocking Ball = " +
                        thread.getName());
                Thread a = new Thread(() -> {
                    try {
                        thread.join();
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    canvas.unpauseAll();
                });
                a.start();


            }

        });

        buttonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        buttonPanel.add(label);
        buttonPanel.add(buttonStart);
        buttonPanel.add(buttonRedBall);
        buttonPanel.add(buttonRace);
        buttonPanel.add(buttonJoin);
        buttonPanel.add(buttonStop);
        content.add(buttonPanel, BorderLayout.SOUTH);
    }
}
