package scheduling;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 *
 * @author leijurv
 */
public class Student {
    static HashMap<Grade, ArrayList<Subject>> allRequiredSubjects;
    final ArrayList<Klass> requiredClasses;
    final ArrayList<Subject> requiredSubjects;
    final String name;
    final Grade grade;
    public Student(String name, Grade grade, ArrayList<Klass> requiredClasses) {
        this.name = name;
        this.grade = grade;
        this.requiredClasses = requiredClasses;
        this.requiredSubjects=new ArrayList<>(allRequiredSubjects.get(grade));
        for(Klass klass : requiredClasses){//if the student is required to take Algebra II, remove the redundant requirement to take one math class
               requiredSubjects.remove(klass.getSubject()) ;
        }
    }
    public Student(String name, Grade grade) {
        this(name, grade, new ArrayList<>());
    }
    public static void unfufilledRequirements(Student student, Roster roster) {
        Stream<Subject> reqSubjects = allRequiredSubjects.get(student.grade).stream();//stream of required subjects
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
    public ArrayList<ArrayList<Section>> requirements(){
        ArrayList<ArrayList<Section>> result=new ArrayList<ArrayList<Section>>();
        for(Klass k : requiredClasses){
            result.add(new ArrayList<>(Arrays.asList(k.sections)));//can take any section of this klass to fufill this requirement
        }
        for(Subject s : requiredSubjects){
            ArrayList<Section> thisSubject=new ArrayList<>();
            for(Klass k : s.klasses){
                thisSubject.addAll(Arrays.asList(k.sections));//can take any section within any klass of this section to fufill the requirement
            }
            result.add(thisSubject);
        }
        return result;
    }
}
