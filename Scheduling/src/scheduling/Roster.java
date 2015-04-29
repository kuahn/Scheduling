package scheduling;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/**
 *
 * @author leijurv
 */
public class Roster {
    public final ArrayList<Student> students;
    public final ArrayList<Section> sections;
    private final HashMap<Section, ArrayList<Student>> roster;
    private final HashMap<Student, ArrayList<Section>> taking;
    private final Object lock = new Object();
    public Roster(ArrayList<Student> students, ArrayList<Section> sections) {
        this.students = students;
        this.sections = sections;
        roster = new HashMap<>();
        taking = new HashMap<>();
        synchronized (lock) {
            for (Student student : students) {
                taking.put(student, new ArrayList<>());
            }
            for (Section section : sections) {
                roster.put(section, new ArrayList<>());
            }
        }
    }
    public void setSection(Student student, Section section) {
        if (!students.contains(student) || !sections.contains(section)) {
            throw new IllegalArgumentException("Not tracking student " + student + " or section " + section);
        }
        synchronized (lock) {
            ArrayList<Section> currentlyTaking = taking.get(student);
            Section conflict = findConflict(section, currentlyTaking);
            if (conflict != null) {
                System.out.println("Conflict while trying to add " + student + " to " + section + ", removing " + conflict);
                remove(student, conflict);
            }
            roster.get(section).add(student);
            taking.get(student).add(section);
        }
    }
    public void remove(Student student, Section section) {
        synchronized (lock) {
            roster.get(section).remove(student);
            taking.get(student).remove(section);
        }
    }
    public Section getSection(Klass klass, Student student) {//In a certian klass, what section is this student in?
        Optional<Section> result = getSections(student).parallelStream().filter(section->section.isIn(klass)).findAny();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }
    public List<Section> getSections(Subject subject, Student student) {//same, but a student can be in multiple sections of the same subject
        return new ArrayList<>(getSections(student)).parallelStream().filter((currentlyTaking)->(currentlyTaking.isIn(subject))).collect(Collectors.toList());
    }
    public ArrayList<Section> getSections(Student student) {
        synchronized (lock) {
            return taking.get(student);
        }
    }
    public static Section findConflict(Section section, ArrayList<Section> current) {
        Optional<Section> result = current.parallelStream().filter(previous->previous.conflictsWith(section)).findAny();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }
    @Override
    public String toString() {
        synchronized (lock) {
            /*Equivalent to
             System.out.println("Class lists:");
             for(Section section : sections){
             System.out.println(section->section + " class list: " + roster.get(section));
             }
             System.out.println();

             but this is more OO (and quite possibly faster?)
             */
            String s = sections.parallelStream().map(section->{
                List<Student> students = roster.get(section);
                return section + " class list (" + students.size() + " students): " + students;
            }).collect(Collectors.joining("\n", "Class lists:\n", "\n"));
            return s;
        }
    }
    public int numStudents(Section section) {
        synchronized (lock) {
            return roster.get(section).size();
        }
    }
    public void reset(Student student) {
        ArrayList<Section> takin = new ArrayList<>(getSections(student));
        for (Section s : takin) {
            remove(student, s);
        }
    }
    public void write(File directory) throws IOException {
        directory.mkdirs();
        for (Section section : sections) {
            File f = new File(directory.toString() + File.separatorChar + section.toString() + ".csv");
            try(FileWriter dank = new FileWriter(f)) {
                dank.write("Student\n");
                for (Student student : roster.get(section)) {
                    dank.write(student.toString());
                    dank.write("\n");
                }
            }
        }
    }
}
