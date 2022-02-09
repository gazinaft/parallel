package ConsoleWriter;

public class ConsoleCounterAsync implements AbstractConsoleCounter {

    final int lines;
    final int lineLength;
    boolean isOver = false;
    int currentChar = 0;
    boolean turn = true;


    public ConsoleCounterAsync(int l, int ll) {
        lines = l;
        lineLength = ll;
    }


    @Override
    public synchronized boolean isOver() {
        return isOver;
    }

    @Override
    public synchronized void write(String s, boolean flag) {
        if (isOver) return;
        System.out.print(s);
        currentChar++;
        turn = !turn;
        if (currentChar % lineLength == 0) System.out.println();

        if (currentChar % (lineLength * lines) == 0) {
            isOver = true;
        }
    }
}
