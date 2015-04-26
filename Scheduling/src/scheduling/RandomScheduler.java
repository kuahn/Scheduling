package scheduling;
import java.util.*;
import java.util.stream.Collectors;
/**
 *
 * @author leijurv
 */
public class RandomScheduler extends Scheduler {
    private Schedule temp;
    public RandomScheduler(ArrayList<Student> students, ArrayList<Subject> subjects) {
        super(students, subjects);
        temp = new Schedule(this.sections, this.students);
    }
    final static int STUDENT_ASSIGN_ATTEMPTS = 5;
    static final int TEACHER_ASSIGN_ATTEMPTS = 10;
    static final int TOTAL_ASSIGN_ATTEMPTS = 1000;
    static final boolean PRINT_ADDS = false;
    static final boolean PRINT_UNF = false;
    static final boolean PRINT_ATT = false;
    static final boolean PRINT_INIT = false;
    int completeAttemptsSoFar = 0;
    @Override
    public void startScheduling() {
        int numAttempts = 0;
        while (assignRandomBlocksAndTeachers() < 0 && numAttempts < TOTAL_ASSIGN_ATTEMPTS) {
            numAttempts++;
        }
        if (numAttempts >= TOTAL_ASSIGN_ATTEMPTS) {
            throw new IllegalStateException("I");
        }
        //System.out.println("Successfully randomly assigned sections to teachers and blocks after " + numAttempts + " attempts");
        if (PRINT_INIT) {
            printSched();
        }
        List<Student> toAssign = students;
        int numAtt = 0;
        while (numAtt < STUDENT_ASSIGN_ATTEMPTS) {
            if (PRINT_ATT) {
                System.out.println("Going to try to assign students " + toAssign);
            }
            List<Student> unassignableStudents = new ArrayList<>();
            for (Student student : toAssign) {
                if (assignStudent(student) > 0) {
                    unassignableStudents.add(student);
                }
            }
            if (PRINT_ATT) {
                System.out.println("Unassignable students: " + unassignableStudents);
            }
            if (unassignableStudents.isEmpty()) {
                break;
            }
            toAssign = unassignableStudents;
            numAtt++;
        }
        if (numAtt >= STUDENT_ASSIGN_ATTEMPTS) {
            //System.out.println("Unassignable students: " + toAssign);
            throw new IllegalStateException("S" + toAssign.size());
        }
        printSched();
        Map<Student, String> studentSchedules = students.stream().parallel().collect(Collectors.toMap(student->student, student->temp.getStudentSchedule(student).toString()));
        System.out.println("Schedules for students: " + studentSchedules);
        temp.roster.print();
        result = temp;
    }
    private void printSched() {
        for (Section section : sections) {
            System.out.println(section + " at " + temp.timings.get(section) + " taught by " + temp.teachers.get(section) + " in " + temp.locations.get(section));
        }
        Map<Teacher, String> teacherSchedules = teachers.stream().parallel().collect(Collectors.toMap(teacher->teacher, teacher->temp.getTeacherSchedule(teacher).toString()));
        System.out.println("Schedules for teachers: " + teacherSchedules);
        try {
            Map<Room, String> roomSchedules = Room.getRooms().stream().parallel().collect(Collectors.toMap(room->room, room->temp.getRoomSchedule(room).toString()));
            System.out.println("Schedules for rooms: " + roomSchedules);
        } catch (IllegalStateException e) {
            System.out.println("OUT OF ROOMS");
            System.exit(0);
        }
    }
    public int assignStudent(Student student) {
        Random r = new Random();
        temp.roster.reset(student);
        List<Requirement> requirements = student.getRequirements();
        int numUn = 0;
        for (Requirement requirement : requirements) {//This cannot be done in a parallel stream because canJoinClass depends on previous class joining
            //The combination of parallel, filter, and findany makes this effectively a random selection, I think
            Optional<Section> toJoin = requirement.getSectionOptionStream().parallel().filter(section->canJoinClass(student, section)).findAny();
            if (!toJoin.isPresent()) {
                if (PRINT_UNF) {
                    System.out.println("Unable to fufill requirement " + requirement + " for student " + student);
                }
                numUn++;
                continue;
            }
            Section section = toJoin.get();
            if (PRINT_ADDS) {
                System.out.println("Adding " + student + " to " + section + " to fufill requirement " + requirement);
            }
            temp.roster.setSection(student, section);
        }
        return numUn;
    }
    public boolean canJoinClass(Student student, Section section) {// TODO implement maximum class size
        if (!section.canFitAnotherStudent(temp.roster.numStudents(section))) {
            return false;
        }
        Block sectionTime = temp.timings.get(section);
        if (sectionTime == null) {
            throw new IllegalArgumentException("must assign section times before class list");
        }
        return temp.getStudentSection(student, sectionTime).isEmpty();//if the student is nowhere else during then that block
    }
    public int assignRandomBlocksAndTeachers() {
        Random rand = new Random();
        int[] roomUsage = new int[Block.numBlocks];
        int numSec = sections.size();
        int numRooms = Room.getRoomArray().length;
        ArrayList<Section> sectionz = new ArrayList<>(sections);
        for (int i = 0; i < numSec; i++) {
            sectionz.add(sectionz.remove(rand.nextInt(numSec)));
        }
        for (int sectionID = 0; sectionID < numSec; sectionID++) {
            Section section = sectionz.get(sectionID);
            ArrayList<Teacher> posss = section.getTeachers();
            Block b = null;
            Teacher t;
            int numAttempts = 0;
            do {
                t = posss.get(rand.nextInt(posss.size()));
                ArrayList<Block> workingBlocks = new ArrayList<>(t.getWorkingBlocks());//some teachers only work some times, remember? grr
                if (workingBlocks.isEmpty()) {
                    continue;
                }
                temp.teachers.put(section, t);
                while (!workingBlocks.isEmpty()) {
                    b = workingBlocks.remove(rand.nextInt(workingBlocks.size()));
                    temp.timings.put(section, b);
                    if (temp.getTeacherLocation(t, b).size() <= 1 && roomUsage[b.blockID] < numRooms) {
                        break;
                    }
                }
                numAttempts++;
            } while ((temp.getTeacherLocation(t, b).size() > 1 || roomUsage[b.blockID] >= numRooms) && numAttempts < TEACHER_ASSIGN_ATTEMPTS);
            if (numAttempts >= TEACHER_ASSIGN_ATTEMPTS) {
                for (int i = 0; i <= sectionID; i++) {//reset all sections up to and including this one
                    temp.teachers.put(sectionz.get(i), null);
                    temp.timings.put(sectionz.get(i), null);
                    temp.locations.put(sectionz.get(i), null);
                }
                return -1;//if unable to assign teachers and blocks, don't even try to assign rooms
            }
            int room = roomUsage[b.blockID]++;
            temp.locations.put(section, Room.getRoomArray()[room]);
            // TODO deal with Klass.getAcceptableRooms()
        }
        return 1;
    }
}
