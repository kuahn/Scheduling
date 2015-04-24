package scheduling;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * @author leijurv
 */
public class Student {
    static HashMap<Grade, ArrayList<Subject>> requiredSubjects;
    final ArrayList<Klass> requiredClasses;
    final String name;
    final Grade grade;
    public Student(String name, Grade grade, ArrayList<Klass> requiredClasses) {
        this.name = name;
        this.grade = grade;
        this.requiredClasses = requiredClasses;
    }
    public Student(String name, Grade grade) {
        this(name, grade, new ArrayList<>());
    }
}
