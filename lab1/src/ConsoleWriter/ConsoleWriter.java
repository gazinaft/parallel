package ConsoleWriter;

public class ConsoleWriter {
    public static void main(String[] args) {
        
        final int charsPerRow = 100;
        final int rows = 100;
        ConsoleCounterAsync ac = new ConsoleCounterAsync(rows, charsPerRow);
        var asyncHorizontal = new ConsoleThread("-", ac, true);
        var asyncVertical = new ConsoleThread("|", ac, false);
        asyncHorizontal.start();
        asyncVertical.start();
        try {
            asyncHorizontal.join();
            asyncVertical.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("=========================================");
        // With Sync
        ConsoleCounter c = new ConsoleCounter(rows, charsPerRow);
        var syncHorizontal = new ConsoleThread("-", c, true);
        var syncVertical = new ConsoleThread("|", c, false);
        syncHorizontal.start();
        syncVertical.start();
    }
}
