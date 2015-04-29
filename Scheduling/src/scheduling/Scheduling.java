/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import scheduling.api.NanoHTTPD;
import scheduling.gooey.Gooey;
/**
 *
 * @author leijurv
 */
public class Scheduling {
    /**
     * @param args the command line arguments
     */
    public static void main1(String[] args) {
        Teacher shrek = new Teacher("Shrek");
        shrek.setDoesWork(0, false);
        shrek.setDoesWork(1, false);
        shrek.setDoesWork(2, false);
        shrek.setDoesWork(3, false);
        shrek.setDoesWork(4, false);
        Teacher donkey = new Teacher("Donkey");
        donkey.setDoesWork(0, false);
        donkey.setDoesWork(2, false);
        Subject math = new Subject("Math", new String[] {"Algebra", "Geometry (dash)", "Cat math"}, new int[] {2, 2, 1}, new Teacher[] {shrek, donkey});
        Subject history = new Subject("History", new String[] {"Swamp history"}, new int[] {2}, new Teacher[] {shrek});
        Student.addRequiredSubject(Grade.GRADE9, math);
        Student.addRequiredSubject(Grade.GRADE9, history);
        Student dragon = new Student("Dragon", Grade.GRADE9, new Klass[] {math.klasses.get(0)});
        ArrayList<Student> students = new ArrayList<>(Arrays.asList(new Student[] {dragon}));
        System.out.println(dragon.getRequirements());
        ArrayList<Subject> subjects = new ArrayList<>(Arrays.asList(new Subject[] {math, history}));
        RandomScheduler rd = new RandomScheduler(students, subjects);
        rd.startScheduling();
    }
    static Subject language;
    static int ti = 0;
    public static void createSubjects(Grade grade, ArrayList<Subject> subjects, ArrayList<Student> students) {
        Random r = new Random();
        String[] subjectn = new String[] {grade + "Math", grade + "History", grade + "English", grade + "Science"};
        for (int i = 0; i < subjectn.length; i++) {
            Teacher a = new Teacher("Teech " + (ti++));
            Teacher b = new Teacher("Teech " + (ti++));
            a.setDoesWork(r.nextInt(7), false);
            b.setDoesWork(r.nextInt(7), false);
            Subject dank = new Subject(subjectn[i], new String[] {"ClassOne", "ClassTwo"}, new int[] {3, 3}, new Teacher[] {a, b});
            Student.addRequiredSubject(grade, dank);
            subjects.add(dank);
        }
        for (int i = 0; i < 100; i++) {
            Student dragon = new Student(grade + "student s" + i, grade);
            students.add(dragon);
        }
    }
    public static double numAt = 0;
    public static double numU = 0;
    public static long time = 0;
    public static RandomScheduler rd;
    public static int numStud = 0;
    public static ArrayList<Student> students;
    public static ArrayList<Subject> subjects;
    public static boolean running = false;
    public static void main2(String[] args) throws IOException {
        students = new ArrayList<>();
        subjects = new ArrayList<>();
        Teacher[] langTeach = new Teacher[] {new Teacher("Teech" + (ti++)), new Teacher("Teech" + (ti++)), new Teacher("Teech" + (ti++))};
        language = new Subject("Language", new String[] {"Spanish 1", "Spanish 2", "Japanese 1", "Japanese 2", "Mandarin 1", "Mandarin 2"}, new int[] {2, 2, 2, 2, 2, 2}, langTeach);
        subjects.add(language);
        Student.addRequiredSubject(Grade.GRADE9, language);
        Student.addRequiredSubject(Grade.GRADE10, language);
        createSubjects(Grade.GRADE9, subjects, students);
        createSubjects(Grade.GRADE10, subjects, students);
        createSubjects(Grade.GRADE11, subjects, students);
        createSubjects(Grade.GRADE12, subjects, students);
        students.add(new Student("Leif Jurvetson", Grade.GRADE9));
        numStud = students.size();
        rd = new RandomScheduler(students, subjects);
        Gooey.setup();
        //System.exit(0);
    }
    public static void main(String[] args) throws IOException {
        NanoHTTPD.init();
        main2(args);
        System.in.read();
    }
    public static String status() {
        String r = "{\"hasSchedule\":" + (getSchedule() != null) + ",\"running\":" + running;
        if (running) {
            r = r + ",\"numAttempts\":" + numAt;
            r = r + ",\"avgNumUnplac\":" + (numU / numAt);
            long currTime = System.currentTimeMillis();
            long diff = currTime - Scheduling.time;
            r = r + ",\"runningTimeMS\":" + diff;
        }
        r = r + "}";
        return r;
    }
    public static void start() {
        if (rd.isFinished() || running) {
            return;
        }
        time = System.currentTimeMillis();
        running = true;
        while (running) {
            try {
                rd.startScheduling();
                break;
            } catch (IllegalStateException e) {
                String x = e.getMessage();
                if (x.startsWith("S")) {
                    try {
                        int numUn = Integer.parseInt(x.substring(1, x.length()));
                        //System.out.println("did");
                        numU += numUn;
                        numAt++;
                    } catch (NumberFormatException ee) {
                        System.out.println(ee);
                        System.out.println(e);
                        continue;
                    }
                }
            }
            Gooey.infotab.repaint();
        }
        if (running) {
            Gooey.onFinish();
            try {
                Schedule.output(rd);
            } catch (IOException ex) {
            }
        }
        running = false;
    }
    public static Schedule getSchedule() {
        return rd.getResult();
    }
}
