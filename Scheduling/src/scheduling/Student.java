package scheduling;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    public static void unfufilledRequirements(Student student, Roster roster) {
        Stream<Subject> reqSubjects = requiredSubjects.get(student.grade).stream();//stream of required subjects
        Stream<Klass> reqKlasses = student.requiredClasses.stream();//stream of required klasses
        reqSubjects = reqSubjects.parallel();//make it parallel to make the filter operation faster
        reqKlasses = reqKlasses.parallel();//this is safe becasue Section.isIn is thread safe
        // TODO check if parallel makes it faster
        ArrayList<Section> currentSections = roster.getSections(student);
        for (Section taking : currentSections) {
            reqSubjects = reqSubjects.filter(sub->!taking.isIn(sub));//filter out subjects that have just been met
            reqKlasses = reqKlasses.filter(klass->!taking.isIn(klass));
        }
        //now all the items in both streams are klasses and subjects that are not satisfied by currentSections
        List<Subject> unfufilledSubjects = reqSubjects.collect(Collectors.toList());
        List<Klass> unfufilledKlasses = reqKlasses.collect(Collectors.toList());
    }
}
