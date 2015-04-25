package scheduling;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 *
 * @author leijurv
 */
public class Student {
    private static final HashMap<Grade, ArrayList<Subject>> allRequiredSubjects;
    final ArrayList<Klass> requiredClasses;
    final ArrayList<Subject> requiredSubjects;
    final String name;
    final Grade grade;
    static{
        allRequiredSubjects = new HashMap<>();
        for (Grade grade : Grade.values()) {
            allRequiredSubjects.put(grade, new ArrayList<>());
        }
    }
    public static void addRequiredSubject(Grade grade, Subject subject) {
        allRequiredSubjects.get(grade).add(subject);
    }
    public static void addRequiredSubject(Subject subject, Grade grade) {//in case you get the argument order wrong =)
        allRequiredSubjects.get(grade).add(subject);
    }
    public Student(String name, Grade grade, ArrayList<Klass> requiredClasses) {
        this.name = name;
        this.grade = grade;
        this.requiredClasses = requiredClasses;
        this.requiredSubjects = new ArrayList<>(allRequiredSubjects.get(grade));
        for (Klass klass : requiredClasses) {//if the student is required to take Algebra II, remove the redundant requirement to take one math class
            requiredSubjects.remove(klass.getSubject());
        }
    }
    public Student(String name, Grade grade, Klass[] requiredClasses) {//to make it easier if you want to pass an array not arraylist
        this(name, grade, new ArrayList<>(Arrays.asList(requiredClasses)));
    }
    public Student(String name, Grade grade) {
        this(name, grade, new ArrayList<>());
    }
    public static ArrayList<ArrayList<Section>> unfufilledRequirements(Student student, Roster roster) {
        Stream<Subject> reqSubjects = allRequiredSubjects.get(student.grade).stream();//stream of required subjects
        Stream<Klass> reqKlasses = student.requiredClasses.stream();//stream of required klasses
        reqSubjects = reqSubjects.parallel();//make it parallel to make the filter operation faster
        reqKlasses = reqKlasses.parallel();//this is safe becasue Section.isIn is thread safe
        // TODO check if parallel makes it faster
        if (roster != null) {
            ArrayList<Section> currentSections = roster.getSections(student);
            for (Section taking : currentSections) {
                reqSubjects = reqSubjects.filter(sub->!taking.isIn(sub));//filter out subjects that have just been met
                reqKlasses = reqKlasses.filter(klass->!taking.isIn(klass));
            }
        }
        //now all the items in both streams are klasses and subjects that are not satisfied by currentSections
        ArrayList<ArrayList<Section>> result = new ArrayList<>(reqKlasses.map(klass->new ArrayList<>(Arrays.asList(klass.sections))).collect(Collectors.toList()));
        //replace each klass with a list of its sections
        List<ArrayList<Section>> fromSub = reqSubjects.map(subject->new ArrayList<>(subject.klasses.stream().parallel().map(klass->Arrays.asList(klass.sections)).flatMap(x->x.stream()).collect(Collectors.toList()))).collect(Collectors.toList());//don't convert outer to arraylist because we are adding to other
        //do the same with subjects
        result.addAll(fromSub);
        return result;
    }
    public ArrayList<ArrayList<Section>> getRequirements() {
        return unfufilledRequirements(this, null);
    }
    /*
     private ArrayList<ArrayList<Section>> calculateRequirements() {
     ArrayList<ArrayList<Section>> result = new ArrayList<>(requiredClasses.stream().map(k->new ArrayList<>(Arrays.asList(k.sections))).collect(Collectors.toList()));
     for (Subject s : requiredSubjects) {
     ArrayList<Section> thisSubject = new ArrayList<>();
     for (Klass k : s.klasses) {
     thisSubject.addAll(Arrays.asList(k.sections));//can take any section within any klass of this section to fufill the requirement
     // TODO see if Java can combine a stream<list> into a list
     }
     result.add(thisSubject);
     }
     return result;
     }*/
}
