package scheduling;
import java.util.ArrayList;
import java.util.Random;
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
        System.out.println("Schedules for teachers: ");
        for (Teacher teacher : teachers) {
            temp.printSchedule(teacher);
        }
    }
    public void assignStudent(Student student) {
        ArrayList<ArrayList<Section>> requirements = student.getRequirements();
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
