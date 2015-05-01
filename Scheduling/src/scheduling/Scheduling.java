/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
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
        Student dragon = new Student("Dragon", Grade.GRADE9, new Klass[] {math.klasses.get(0)}, Gender.MAIL);
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
            Student dragon = new Student(grade + "student s" + i, grade, Gender.get(r.nextBoolean()));
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
    public static ArrayList<Teacher> teachers;
    public static boolean running = false;
    public static void main2(String[] args) throws IOException, InterruptedException {
        students = new ArrayList<>();
        subjects = new ArrayList<>();
        Teacher[] langTeach = new Teacher[] {new Teacher("Teech " + (ti++)), new Teacher("Teech " + (ti++)), new Teacher("Teech " + (ti++))};
        language = new Subject("Language", new String[] {"Spanish 1", "Spanish 2", "Japanese 1", "Japanese 2", "Mandarin 1", "Mandarin 2"}, new int[] {2, 2, 2, 2, 2, 2}, langTeach);
        subjects.add(language);
        Student.addRequiredSubject(Grade.GRADE9, language);
        Student.addRequiredSubject(Grade.GRADE10, language);
        createSubjects(Grade.GRADE9, subjects, students);
        createSubjects(Grade.GRADE10, subjects, students);
        createSubjects(Grade.GRADE11, subjects, students);
        createSubjects(Grade.GRADE12, subjects, students);
        numStud = students.size();
        rd = new RandomScheduler(students, subjects);
        teachers = rd.teachers;
        save();
        Gooey.setup();
        Thread.sleep(10000);
        onAddStudent(new Student("Leif Jurvetson", Grade.GRADE9, Gender.MAIL));
        //System.exit(0);
    }
    public static void main3(String[] args) throws IOException {
        read();
        numStud = students.size();
        rd = new RandomScheduler(students, subjects);
        //save();
        Gooey.setup();
    }
    public static void save() throws IOException {
        String base = System.getProperty("user.home") + "/Documents/saveFile";
        File dank = new File(base);
        DataOutputStream shrek = new DataOutputStream(new FileOutputStream(dank));
        shrek.writeInt(teachers.size());
        for (Teacher t : teachers) {
            t.write(shrek);
        }
        shrek.writeInt(subjects.size());
        for (Subject s : subjects) {
            s.write(shrek);
        }
        for (int grade = 9; grade <= 12; grade++) {
            ArrayList<SubjectRequirement> req = Student.getSubjectRequirements(grade);
            shrek.writeInt(req.size());
            for (int i = 0; i < req.size(); i++) {
                shrek.writeUTF(req.get(i).subject.name);
            }
        }
        shrek.writeInt(students.size());
        for (Student s : students) {
            s.write(shrek);
        }
    }
    public static void read() throws IOException {
        String base = System.getProperty("user.home") + "/Documents/saveFile";
        File dank = new File(base);
        DataInputStream shrek = new DataInputStream(new FileInputStream(dank));
        int numTeachers = shrek.readInt();
        teachers = new ArrayList<>(numTeachers);
        for (int i = 0; i < numTeachers; i++) {
            teachers.add(Teacher.read(shrek));
        }
        int numSubjects = shrek.readInt();
        subjects = new ArrayList<>(numSubjects);
        for (int i = 0; i < numSubjects; i++) {
            Subject subject = Subject.read(shrek);
            subjects.add(subject);
        }
        for (int grade = 9; grade <= 12; grade++) {
            Grade grad = Grade.swamplord420noscope(grade);
            int numReq = shrek.readInt();
            for (int i = 0; i < numReq; i++) {
                String subjectname = shrek.readUTF();
                Subject subject = getSubject(subjectname);
                Student.addRequiredSubject(grad, subject);
                System.out.println(grad + "," + subject);
            }
        }
        int numStudents = shrek.readInt();
        students = new ArrayList<>(numStudents);
        for (int i = 0; i < numStudents; i++) {
            students.add(Student.read(shrek));
        }
    }
    public static <Swamp> Swamp get(Optional<Swamp> result) {
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }
    public static Subject getSubject(String subjectName) {
        return subjects.parallelStream().filter(subject->subject.name.equals(subjectName)).findAny().get();
    }
    public static Teacher getTeacher(String nuevaUsername) {
        return teachers.parallelStream().filter(teacher->teacher.name.equals(nuevaUsername)).findAny().get();
    }
    public static Klass getKlass(String klassName) {
        return subjects.parallelStream().flatMap(subject->subject.klasses.parallelStream()).filter(klass->klass.toString().equals(klassName)).findAny().get();
    }
    public static void onAddStudent(Student student) {
        students.add(student);
        rd.onAddStudent(student);
    }
    public static void main(String[] args) throws Exception {
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
