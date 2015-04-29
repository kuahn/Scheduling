package scheduling;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
/**
 *
 * @author leijurv
 */
public abstract class Scheduler {
    public final ArrayList<Teacher> teachers;
    public final ArrayList<Subject> subjects;
    public final ArrayList<Klass> klasses;
    public final ArrayList<Section> sections;
    public final ArrayList<Student> students;
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
        this.teachers = klasses.stream().flatMap(klass->klass.teachers.stream()).distinct().collect(Collectors.toCollection(()->new ArrayList<>()));
    }
    public abstract void startScheduling();
    public boolean isFinished() {
        return result != null;
    }
    public Schedule getResult() {
        return result;
    }
}
