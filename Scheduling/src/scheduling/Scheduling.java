/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
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
        Subject math = new Subject("Math", new String[] {"Algebra", "Geometry (dash)", "Cat math"}, new int[] {2, 2, 1}, new Teacher[] {shrek, donkey}, 20);
        Subject history = new Subject("History", new String[] {"Swamp history"}, new int[] {2}, new Teacher[] {shrek}, 20);
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
        String[] subjectn = new String[] {grade + "Math", grade + "History", grade + "English", grade + "Science"};
        for (int i = 0; i < subjectn.length; i++) {
            Teacher a = new Teacher("Teech" + (ti++));
            Teacher b = new Teacher("Teech" + (ti++));
            Subject dank = new Subject(subjectn[i], new String[] {"ClassOne", "ClassTwo"}, new int[] {3, 3}, new Teacher[] {a, b}, 19);
            Student.addRequiredSubject(grade, dank);
            subjects.add(dank);
        }
        for (int i = 0; i < 100; i++) {
            Student dragon = new Student(grade + "student" + i, grade);
            students.add(dragon);
        }
    }
    public static void main(String[] args) throws IOException {
        ArrayList<Student> students = new ArrayList<>();
        ArrayList<Subject> subjects = new ArrayList<>();
        Teacher[] langTeach = new Teacher[] {new Teacher("Teech" + (ti++)), new Teacher("Teech" + (ti++)), new Teacher("Teech" + (ti++))};
        language = new Subject("Language", new String[] {"Spanish 1", "Spanish 2", "Japanese 1", "Japanese 2", "Mandarin 1", "Mandarin 2"}, new int[] {2, 2, 2, 2, 2, 2}, langTeach, 18);
        subjects.add(language);
        Student.addRequiredSubject(Grade.GRADE9, language);
        Student.addRequiredSubject(Grade.GRADE10, language);
        createSubjects(Grade.GRADE9, subjects, students);
        createSubjects(Grade.GRADE10, subjects, students);
        createSubjects(Grade.GRADE11, subjects, students);
        createSubjects(Grade.GRADE12, subjects, students);
        RandomScheduler rd = new RandomScheduler(students, subjects);
        double numAt = 0;
        double numU = 0;
        int numStud = students.size();
        while (true) {
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
                System.out.println("Just placed " + x.substring(1, x.length()) + " of " + 400);
                System.out.println("On average, " + Math.floor(numU / (numAt) * 100) / 100 + " of " + students.size() + ", or " + toPercent(numU / (numAt * students.size())) + " of students, are unplacable. (" + numAt + " attempts)");
            }
        }
        output(rd);
    }
    public static void output(Scheduler rd) throws IOException {
        Schedule schedule = rd.getResult();
        Map<Teacher, Map<Block, Section>> teacherSchedules = rd.teachers.stream().parallel().collect(Collectors.toMap(teacher->teacher, teacher->schedule.getTeacherSchedule(teacher)));
        Map<Room, Map<Block, Section>> roomSchedules = Room.getRooms().stream().parallel().collect(Collectors.toMap(room->room, room->schedule.getRoomSchedule(room)));
        Map<Student, Map<Block, Section>> studentSchedules = rd.students.stream().parallel().collect(Collectors.toMap(student->student, student->schedule.getStudentSchedule(student)));
        String basePath = System.getProperty("user.home") + "/Documents/schedout/";
        File base = new File(basePath);
        File main = new File(basePath + "sections.csv");
        schedule.roster.write(new File(basePath + "roster"));
        try(FileWriter writer = new FileWriter(main)) {
            writer.write("Section,Block,Teacher,Room\n");
            for (Section section : schedule.sections) {
                writer.write(section + "," + schedule.timings.get(section).blockID + "," + schedule.teachers.get(section) + "," + schedule.locations.get(section).roomNumber + "\n");
            }
        }
        new File(basePath + "teachers").mkdir();
        new File(basePath + "students").mkdir();
        new File(basePath + "rooms").mkdir();
        for (Teacher teacher : rd.teachers) {
            write(base, teacherSchedules.get(teacher), schedule, "teachers", teacher.toString());
        }
        for (Student student : rd.students) {
            write(base, studentSchedules.get(student), schedule, "students", student.toString());
        }
        for (Room room : Room.getRoomArray()) {
            write(base, roomSchedules.get(room), schedule, "rooms", room.toString());
        }
    }
    public static void write(File main, Map<Block, Section> data, Schedule schedule, String folderName, String thisName) throws IOException {
        main = new File(main.toString() + File.separatorChar + folderName + File.separatorChar + thisName + ".csv");
        try(FileWriter writer = new FileWriter(main)) {
            writer.write("Section,Block,Teacher,Room\n");
            for (Block b : data.keySet()) {
                Section section = data.get(b);
                writer.write(section + "," + schedule.timings.get(section).blockID + "," + schedule.teachers.get(section) + "," + schedule.locations.get(section).roomNumber + "\n");
            }
        }
    }
    public static String toPercent(double v) {
        return Math.floor(v * 10000) / 100 + "%";
    }
}
