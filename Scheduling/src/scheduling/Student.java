package scheduling;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * @author leijurv
 */
public class Student {
    static HashMap<Grade, ArrayList<Subject>> requiredSubjects;
    ArrayList<Klass> requiredClasses;
    final String name;
    final Grade grade;
    public Student(String name, Grade grade) {
        this.name = name;
        this.grade = grade;
    }
}
