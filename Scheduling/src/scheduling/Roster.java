package scheduling;
import java.util.ArrayList;
import java.util.HashMap;
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
    public Roster(ArrayList<Student> students, ArrayList<Section> sections) {
        this.students = students;
        this.sections = sections;
        roster = new HashMap<>();
        taking = new HashMap<>();
        for (Student student : students) {
            taking.put(student, new ArrayList<>());
        }
        for (Section section : sections) {
            roster.put(section, new ArrayList<>());
        }
    }
    public void setSection(Student student, Section section) {
        if (!students.contains(student) || !sections.contains(section)) {
            throw new IllegalArgumentException("Not tracking student " + student + " or section " + section);
        }
        ArrayList<Section> currentlyTaking = taking.get(student);
        Section conflict = findConflict(section, currentlyTaking);
        if (conflict != null) {
            remove(student, conflict);
        }
        roster.get(section).add(student);
        taking.get(student).add(section);
    }
    public void remove(Student student, Section section) {
        roster.get(section).remove(student);
        taking.get(student).remove(section);
    }
    public Section getSection(Klass klass, Student student) {//In a certian klass, what section is this student in?
        for (Section section : taking.get(student)) {
            if (section.isIn(klass)) {
                return section;
            }
        }
        return null;
    }
    public ArrayList<Section> getSections(Subject subject, Student student) {//same, but a student can be in multiple sections of the same subject
        return new ArrayList<>(taking.get(student).stream().parallel().filter((currentlyTaking)->(currentlyTaking.isIn(subject))).collect(Collectors.toList()));
    }
    public ArrayList<Section> getSections(Student student) {
        return taking.get(student);
    }
    public static Section findConflict(Section section, ArrayList<Section> currentlyTaking) {
        for (Section previous : currentlyTaking) {
            if (previous.conflictsWith(section)) {
                return previous;
            }
        }
        return null;
    }
}
