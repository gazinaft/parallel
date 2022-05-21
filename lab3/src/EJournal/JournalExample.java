package EJournal;

public class JournalExample {
    public static void main(String[] args) {
        var groups = new String[]{"IP-91", "IP-92", "IP-96"};
        var jornal = new Journal(groups);
        var lecturer = new Lecturer(groups, jornal, (x) -> 100);
        var assistant1 = new Assistant(groups[0], jornal, (x) -> 0);
        var assistant2 = new Assistant(groups[1], jornal, (x) -> x * 2);
        var assistant3 = new Assistant(groups[2], jornal, (x) -> 100 - x);
        var threads = new Thread[]{(new Thread(lecturer)),
        (new Thread(assistant1)),
        (new Thread(assistant2)),
        (new Thread(assistant3))
        };
        for (var th : threads) {
            th.start();
        }
        for (var th : threads) {
            try {
                th.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        jornal.printAllGrades();
    }
}
