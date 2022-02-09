package ConsoleWriter;

public class ConsoleCounter implements AbstractConsoleCounter {
    int currentChar;
    final int lineLength;
    final int limit;
    boolean isOver = false;
    boolean order = true;

    ConsoleCounter(int l, int ll) {
        currentChar = 0;
        limit = l;
        lineLength = ll;
    }

    @Override
    public synchronized boolean isOver() {
        return isOver;
    }

    @Override
    public synchronized void write(String s, boolean flag) {
        while(flag != order) {
            try {
                wait();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        if (isOver) return;
        System.out.print(s);
        currentChar++;
        order = !order;
        if (currentChar % lineLength == 0) System.out.println();
        if (currentChar % (lineLength * limit) == 0) {
            isOver = true;
        }
        notifyAll();
    }

}
