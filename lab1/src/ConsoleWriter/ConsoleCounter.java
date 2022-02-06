package ConsoleWriter;

public class ConsoleCounter {
    int line;
    final int limit;

    ConsoleCounter(int l) {
        line = 0;
        limit = l;
    }

    synchronized boolean isOver() {
        return limit <= line;
    }

    synchronized void increment() {
        line += 1;
    }

    void refresh() {
        line = 0;
    }
}
