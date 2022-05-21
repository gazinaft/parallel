package EJournal;

import java.util.Random;
import java.util.function.Function;

public class Lecturer implements Runnable {

    private String[] groups;
    private Journal journal;
    private Function<Integer, Integer> f;

    public Lecturer(String[] groups, Journal journal, Function<Integer, Integer> f) {
        this.groups = groups;
        this.journal = journal;
        this.f = f;
    }

    @Override
    public void run() {
        Random r = new Random();
        for (int i = 0; i < Journal.GRADES_PER_TERM /2; i++) {
            for (var group : groups) {
                for (int j = 0; j < Journal.STUDENTS_PER_GROUP; j++) {
                    journal.grade(group, j, f.apply(j));
                    System.out.println("Lecturer Graded " + group + " " + j  + " student");
                }
            }
            try {
                Thread.sleep(r.nextInt(500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
