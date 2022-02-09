public class BallThread extends Thread {
    private final Ball b;

    public BallThread(Ball ball){
        b = ball;
    }

    public BallThread(Ball ball, int priority) {
        b = ball;
        Thread.currentThread().setPriority(priority);
    }

    void iterate(int start, int end) {
        try {
            for(int i = start; i < end; i++){
                if (b.isGone()) {
                    return;
                }
                if (b.isPaused()) {
                    waitForContinuation();
                }
                b.move();
                System.out.println("Thread name = "
                        + Thread.currentThread().getName());
                System.out.println("Priority: " + Thread.currentThread().getPriority());
                Thread.sleep(5);
            }

        } catch(InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    void waitForContinuation() {
        while (!b.isPaused()) {
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        iterate(1, 10000);
    }
}