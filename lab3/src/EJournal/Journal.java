package EJournal;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

public class Journal {

    public static final int GRADES_PER_TERM = 10;
    public static final int STUDENTS_PER_GROUP = 10;

    HashMap<String, ArrayBlockingQueue<Integer>[]> students;

    public Journal(String[] groupNames) {
        students = new HashMap<>();
        for (var gn: groupNames) {
            var gradesPerGroup = new ArrayBlockingQueue[STUDENTS_PER_GROUP];
            for (int i = 0; i < STUDENTS_PER_GROUP; ++i) {
                gradesPerGroup[i] = new ArrayBlockingQueue<Integer>(GRADES_PER_TERM);
            }
            students.put(gn, gradesPerGroup);
        }
    }

    public void grade(String groupName, int person, int grade) {
        try {
            students.get(groupName)[person].put(grade);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Object[] getGradesFor(String groupName, int person) {
        return students.get(groupName)[person].toArray();
    }

    public void printAllGrades() {
        for (var group : students.keySet()) {
            System.out.println(group);
            for (int i = 0; i < STUDENTS_PER_GROUP; i++) {
                System.out.println(students.get(group)[i]);
            }
        }
    }

}
