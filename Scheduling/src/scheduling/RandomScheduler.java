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
        while (assignRandomBlocksAndTeachers() < 0 && numAttempts < 1000) {
            numAttempts++;
        }
        System.out.println("Successfully randomly assigned sections to teachers and blocks after " + numAttempts + " attempts");
        System.out.println("Timings: " + temp.timings);
        System.out.println("Teachers: " + temp.teachers);
        System.out.println("Locations: " + temp.locations);
        Map<Teacher, String> teacherSchedules = teachers.stream().parallel().collect(Collectors.toMap(teacher->teacher, teacher->temp.getTeacherSchedule(teacher).toString()));
        System.out.println("Schedules for teachers: " + teacherSchedules);
        Map<Room, String> roomSchedules = Room.getRooms().stream().parallel().collect(Collectors.toMap(room->room, room->temp.getRoomSchedule(room).toString()));
        System.out.println("Schedules for rooms: " + roomSchedules);
        Stream<Student> unassignable = students.stream().filter(student->assignStudent(student) > 0);
        List<Student> unassignableStudents = unassignable.collect(Collectors.toList());
        System.out.println("Unassignable students: " + unassignableStudents);
        Map<Student, String> studentSchedules = students.stream().parallel().collect(Collectors.toMap(student->student, student->temp.getStudentSchedule(student).toString()));
        System.out.println("Schedules for students: " + studentSchedules);
    }
    public int assignStudent(Student student) {
        Random r = new Random();
        List<Requirement> requirements = student.getRequirements(temp.roster);
        int numUn = 0;
        for (Requirement requirement : requirements) {//This cannot be done in a parallel stream because canJoinClass depends on previous class joining
            //The combination of parallel, filter, and findany makes this effectively a random selection, I think
            Optional<Section> toJoin = requirement.getSectionOptionStream().parallel().filter(section->canJoinClass(student, section)).findAny();
            if (!toJoin.isPresent()) {
                System.out.println("Unable to fufill requirement " + requirement + " for student " + student);
                numUn++;
                continue;
            }
            Section section = toJoin.get();
            System.out.println("Adding " + student + " to " + section + " to fufill requirement " + requirement);
            temp.roster.setSection(student, section);
        }
        return numUn;
    }
    public boolean canJoinClass(Student student, Section section) {// TODO implement maximum class size
        Block sectionTime = temp.timings.get(section);
        if (sectionTime == null) {
            throw new IllegalArgumentException("must assign section times before class list");
        }
        return temp.getStudentSection(student, sectionTime).isEmpty();//if the student is nowhere else during then that block
    }
    static final int ASSIGN_ATTEMPTS = 1000;
    public int assignRandomBlocksAndTeachers() {
        Random rand = new Random();
        int[] roomUsage = new int[Block.numBlocks];
        int numSec = sections.size();
        for (int sectionID = 0; sectionID < numSec; sectionID++) {
            Section section = sections.get(sectionID);
            ArrayList<Teacher> posss = section.getTeachers();
            Block b = null;
            Teacher t;
            int numAttempts = 0;
            do {
                t = posss.get(rand.nextInt(posss.size()));
                ArrayList<Block> workingBlocks = t.getWorkingBlocks();//some teachers only work some times, remember? grr
                if (workingBlocks.isEmpty()) {
                    continue;
                }
                temp.teachers.put(section, t);
                for (Block c : workingBlocks) {
                    b = c;
                    temp.timings.put(section, b);
                    if (temp.getTeacherLocation(t, b).size() <= 1) {
                        roomUsage[b.blockID]++;
                        break;
                    }
                }
                numAttempts++;
            } while (temp.getTeacherLocation(t, b).size() > 1 && numAttempts < ASSIGN_ATTEMPTS);
            if (numAttempts >= ASSIGN_ATTEMPTS) {
                for (int i = 0; i <= sectionID; i++) {//reset all sections up to and including this one
                    temp.teachers.put(sections.get(i), null);
                    temp.timings.put(sections.get(i), null);
                    temp.locations.put(sections.get(i), null);
                }
                return -1;//if unable to assign teachers and blocks, don't even try to assign rooms
            }
            int room = roomUsage[b.blockID] - 1;
            temp.locations.put(section, Room.getRoomArray()[room]);
            // TODO deal with Klass.getAcceptableRooms()
        }
        return 1;
    }
}
