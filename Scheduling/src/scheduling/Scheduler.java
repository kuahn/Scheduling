package scheduling;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
/**
 *
 * @author leijurv
 */
public abstract class Scheduler {
    final ArrayList<Teacher> teachers;
    final ArrayList<Subject> subjects;
    final ArrayList<Klass> klasses;
    final ArrayList<Section> sections;
    final ArrayList<Student> students;
    protected Schedule result = null;
    public Scheduler(ArrayList<Student> students, ArrayList<Subject> subjects) {
        this.subjects = subjects;
        this.students = students;
        klasses = new ArrayList<>();
        this.subjects.stream().forEach(subject->{
            klasses.addAll(subject.klasses);
        });
        sections = new ArrayList<>();
        klasses.stream().forEach(klass->{
            sections.addAll(Arrays.asList(klass.sections));
        });
        this.teachers = new ArrayList<>(klasses.stream().flatMap(klass->klass.teachers.stream()).distinct().collect(Collectors.toList()));
    }
    public abstract void startScheduling();
    public boolean isFinished() {
        return result != null;
    }
    public Schedule getResult() {
        return result;
    }
}
