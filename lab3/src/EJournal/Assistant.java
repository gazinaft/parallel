package EJournal;

import java.util.Random;
import java.util.function.Function;

public class Assistant implements Runnable {

    private String group;
    private Journal journal;
    private Function<Integer, Integer> f;

    public Assistant(String group, Journal journal, Function<Integer, Integer> f) {
        this.group = group;
        this.journal = journal;
        this.f = f;
    }

    @Override
    public void run() {
        Random r = new Random();
        for (int i = 0; i < Journal.GRADES_PER_TERM /2; i++) {
            for (int j = 0; j < Journal.STUDENTS_PER_GROUP; j++) {
                journal.grade(group, j, f.apply(j));
                System.out.println("Assistant Graded " + group + " " + j  + " student");
            }
            try {
                Thread.sleep(r.nextInt(500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
