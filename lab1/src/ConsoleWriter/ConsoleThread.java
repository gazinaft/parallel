package ConsoleWriter;

public class ConsoleThread extends Thread {

    private final String str;
    private final AbstractConsoleCounter counter;
    private final boolean turnOrder;

    ConsoleThread(String s, AbstractConsoleCounter acc, boolean flag) {
        str = s;
        counter = acc;
        turnOrder = flag;
    }

    @Override
    public void run() {
        while (!counter.isOver()) {
            counter.write(str, turnOrder);
        }
    }
}
