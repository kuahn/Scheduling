package scheduling;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 *
 * @author leijurv
 */
public class Student {
    private static final HashMap<Grade, ArrayList<SubjectRequirement>> allRequiredSubjects;
    final ArrayList<Requirement> requirements;
    final String name;
    final Grade grade;
    static{
        allRequiredSubjects = new HashMap<>();
        for (Grade grade : Grade.values()) {
            allRequiredSubjects.put(grade, new ArrayList<>());
        }
    }
    public static void addRequiredSubject(Grade grade, Subject subject) {
        allRequiredSubjects.get(grade).add(new SubjectRequirement(subject, grade + " " + subject + " requirement"));
    }
    public static void addRequiredSubject(Subject subject, Grade grade) {//in case you get the argument order wrong =)
        addRequiredSubject(grade, subject);
    }
    public Student(String name, Grade grade, ArrayList<Klass> requiredClasses) {
        this.name = name;
        this.grade = grade;
        requirements = new ArrayList<>(allRequiredSubjects.get(grade));
        for (Klass klass : requiredClasses) {//if the student is required to take Algebra II, remove the redundant requirement to take one math class
            requirements.remove(klass.getSubject());
        }
    }
    public Student(String name, Grade grade, Klass[] requiredClasses) {//to make it easier if you want to pass an array not arraylist
        this(name, grade, new ArrayList<>(Arrays.asList(requiredClasses)));
    }
    public Student(String name, Grade grade) {
        this(name, grade, new ArrayList<>());
    }
    public static ArrayList<Requirement> unfufilledRequirements(Student student, Roster roster) {
        return new ArrayList<>(unfufilledRequirementsStream(student, roster).collect(Collectors.toList()));
    }
    public static Stream<Requirement> unfufilledRequirementsStream(Student student, Roster roster) {
        Stream<Requirement> reqs = student.requirements.stream();
        reqs = reqs.parallel();//make it parallel to make the filter operation faster
        // TODO check if parallel makes it faster
        if (roster != null) {
            ArrayList<Section> currentSections = roster.getSections(student);
            for (Section taking : currentSections) {
                reqs = reqs.filter(req->!req.fufilledBy(taking));//filter out subjects that have just been met
            }
        }
        //now all the items in both streams are klasses and subjects that are not satisfied by currentSections
        return reqs;
//^^ AVE GLORIOSA EN NOMINE STREAMS ^^
        //WE PRAISE OUR GLORIOUS LORD STREAM
        //MAY HE GRANT US PARALELL PROCESSING AND REDUCING
        //ALL HAIL STREAMS
    }
    public ArrayList<Requirement> getRequirements() {
        return unfufilledRequirements(this, null);
    }
    public Stream<Requirement> getRequirementStream() {
        return unfufilledRequirementsStream(this, null);
    }
    public ArrayList<Requirement> getRequirements(Roster r) {
        return unfufilledRequirements(this, r);
    }
    public Stream<Requirement> getRequirementStream(Roster r) {
        return unfufilledRequirementsStream(this, r);
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
    @Override
    public String toString() {
        return name;
    }
}
