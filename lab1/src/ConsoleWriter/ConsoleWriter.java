package ConsoleWriter;

public class ConsoleWriter {
    public static void main(String[] args) {
        
        final int charsPerRow = 50;
        final int rows = 100;
        ConsoleCounter c = new ConsoleCounter(rows);

        Thread horizontal = new Thread(() -> {
            while (!c.isOver()) {
                System.out.print('-');
                c.increment();
            }
        });

        Thread vertical = new Thread(() -> {
            while (!c.isOver()) {
                System.out.print('|');
                c.increment();
            }
        });
        horizontal.start();
        vertical.start();
        System.out.println("");

        // With Sync

        c.refresh();

        Thread horizontalSync = new SyncWriter("-", c);
        Thread verticalSync = new SyncWriter("|", c);
        horizontalSync.start();
        verticalSync.start();
    }
}
