package scheduling;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 *
 * @author leijurv
 */
public class Student {
    private static final HashMap<Grade, ArrayList<SubjectRequirement>> allRequiredSubjects;
    public final ArrayList<Requirement> requirements;
    public final String name;
    public final String firstName;
    public final String lastName;//if the name is ellie van der rine, the lastname would be "van der rine" and the firstname would be "ellie"
    public final String nuevaUsername;
    public final Grade grade;
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
        int l = name.indexOf(' ');
        if (l != -1) {
            firstName = name.substring(0, l);
            lastName = name.substring(l + 1, name.length());
        } else {
            firstName = name;
            lastName = "";
        }
        String cut = lastName.split(" ")[0];//elllie van der rijn goes to elivan not elivand
        nuevaUsername = (firstName.length() < 3 ? firstName : firstName.substring(0, 3)) + (cut.length() < 4 ? cut : cut.substring(0, 4)).toLowerCase();
        this.grade = grade;
        requirements = new ArrayList<>(allRequiredSubjects.get(grade));
        for (Klass klass : requiredClasses) {//if the student is required to take Algebra II, remove the redundant requirement to take one math class
            requirements.remove(new SubjectRequirement(klass.getSubject(), null));//this works because SubjectRequirement.equals only checks if their subjects are equal
            requirements.add(new KlassRequirement(klass));
        }
    }
    public Student(String name, Grade grade, Klass[] requiredClasses) {//to make it easier if you want to pass an array not arraylist
        this(name, grade, new ArrayList<>(Arrays.asList(requiredClasses)));
    }
    public Student(String name, Grade grade) {
        this(name, grade, new ArrayList<>());
    }
    public int getNumFreeBlocks() {
        return Block.numBlocks - requirements.size();
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
    @Override
    public String toString() {
        return name;
    }
    public String getinfo(Schedule s) {//used for api
        Map<Block, List<Section>> schedule = s.getStudentSchedule(this);
        StringBuilder resp = new StringBuilder();
        resp.append("{\n");
        aq(resp, "firstname");
        resp.append(':');
        aq(resp, firstName);
        resp.append(',');
        resp.append('\n');
        aq(resp, "lastname");
        resp.append(':');
        aq(resp, lastName);
        resp.append(',');
        resp.append('\n');
        aq(resp, "username");
        resp.append(':');
        aq(resp, nuevaUsername);
        resp.append(',');
        resp.append('\n');
        aq(resp, "grade");
        resp.append(':');
        aq(resp, grade.toString());
        resp.append(',');
        resp.append('\n');
        aq(resp, "schedule");
        resp.append(":{\n");
        for (Block b : Block.blocks) {
            aq(resp, b.toString());
            resp.append(':');
            List<Section> location = schedule.get(b);
            if (location == null) {
                location = new ArrayList<>();
            }
            List<String> res = location.parallelStream().map(section->'"' + section.toString() + '"').collect(Collectors.toList());
            resp.append(res.toString());
            resp.append(",\n");
        }
        resp.append("}}");
        return resp.toString();
    }
    public static void aq(StringBuilder r, String s) {//append something within quotes, helper for JSON creation
        // TODO use that JSON library to make JSON
        r.append("\"");
        r.append(s);
        r.append("\"");
    }
}
