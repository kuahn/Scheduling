package scheduling;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 *
 * @author leijurv
 */
public class RandomScheduler extends Scheduler {
    private Schedule temp;
    public RandomScheduler(ArrayList<Teacher> teachers, ArrayList<Student> students, ArrayList<Subject> subjects) {
        super(teachers, students, subjects);
        temp = new Schedule(this.sections, this.students);
    }
    @Override
    public void startScheduling() {
        int numAttempts = 0;
        while (!assignRandomBlocksAndTeachers() && numAttempts < 100) {
            numAttempts++;
        }
        //int[] sectionsDuringBlocks = new int[Block.blocks];
        System.out.println("Successfully randomly assigned sections to teachers and blocks after " + numAttempts + " attempts");
        System.out.println("Timings: " + temp.timings);
        System.out.println("Teachers: " + temp.teachers);
        Map<Teacher, String> teacherSchedules = teachers.stream().collect(Collectors.toMap(teacher->teacher, teacher->temp.printSchedule(teacher)));
        System.out.println("Schedules for teachers: " + teacherSchedules);
        Stream<Student> unassignable = students.stream().filter(student->!assignStudent(student));
        List<Student> unassignableStudents = unassignable.collect(Collectors.toList());
        System.out.println("Unassignable students: " + unassignableStudents);
    }
    public boolean assignStudent(Student student) {
        Random r = new Random();
        ArrayList<ArrayList<Section>> requirements = student.getRequirements();
        for (ArrayList<Section> klass : requirements) {
            //The combination of parallel, filter, and findany makes this effectively a random selection, I think
            Optional<Section> toJoin = klass.stream().parallel().filter(section->canJoinClass(student, section)).findAny();
            if (!toJoin.isPresent()) {
                System.out.println("Unable to fufill requirement " + klass + " for student " + student);
                return false;
            }
            Section section = toJoin.get();
            System.out.println("Adding " + student + " to " + section + " to fufill requirement " + klass);
            temp.roster.setSection(student, section);
        }
        return true;
    }
    public boolean canJoinClass(Student student, Section section) {
        Block sectionTime = temp.timings.get(section);
        if (sectionTime == null) {
            throw new IllegalArgumentException("must assign section times before class list");
        }
        return temp.getStudentSection(student, sectionTime).isEmpty();//if the student is nowhere else during then that block
    }
    public boolean assignRandomBlocksAndTeachers() {
        Random r = new Random();
        for (Section section : sections) {
            ArrayList<Teacher> posss = section.getTeachers();
            Block b;
            Teacher t;
            int numAttempts = 0;
            do {
                t = posss.get(r.nextInt(posss.size()));
                ArrayList<Block> workingBlocks = t.getWorkingBlocks();//some teachers only work some times, remember? grr
                b = workingBlocks.get(r.nextInt(workingBlocks.size()));
                temp.teachers.put(section, t);
                temp.timings.put(section, b);
                numAttempts++;
            } while (temp.getTeacherLocation(t, b).size() > 1 && numAttempts < 100);
            if (numAttempts >= 100) {
                temp.teachers.put(section, null);
                temp.timings.put(section, null);
                return false;
            }
        }
        return true;
    }
}
