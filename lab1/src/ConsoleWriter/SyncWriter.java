package ConsoleWriter;

public class SyncWriter extends Thread {

    private final String s;
    private final ConsoleCounter cc;

    SyncWriter(String toWrite, ConsoleCounter counter) {
        s = toWrite;
        cc = counter;
    }

    synchronized void Write() {
        System.out.print(s);
        cc.increment();
    }

    @Override
    public void run() {
        while (!cc.isOver()) {
            Write();
            Thread.yield();
//            cc.notify();
//            try {
//                cc.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            cc.notify();
        }
    }
}
