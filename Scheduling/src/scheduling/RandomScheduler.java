package scheduling;
import java.util.*;
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
    static final int TOTAL_ASSIGN_ATTEMPTS1 = 10;
    static final boolean PRINT_ADDS = false;
    static final boolean PRINT_UNF = false;
    static final boolean PRINT_ATT = true;
    static final boolean PRINT_INIT = false;
    static final boolean PRINT_INIT_TIME = true;
    @Override
    public void startScheduling() {
        for (Section s : sections) {
            System.out.println(s + ":" + s.getTeachers());
        }
        if (isFinished()) {
            System.out.println("Already finished");
            return;
        }
        int numAttempts = 0;
        long time = System.currentTimeMillis();
        while (assignRandomBlocksAndTeachers() < 0 && numAttempts < TOTAL_ASSIGN_ATTEMPTS1) {
            numAttempts++;
        }
        long after = System.currentTimeMillis();
        if (PRINT_INIT_TIME) {
            System.out.println("Inital took" + (after - time) + "ms, " + (numAttempts + 1) + " attempts");
        }
        if (numAttempts >= TOTAL_ASSIGN_ATTEMPTS1) {
            throw new IllegalStateException("I");
        }
        if (!temp.verifyRoomsTeachers()) {
            throw new ArrayIndexOutOfBoundsException("jankydank. verification for first step failed. you broke something");
        }
        //System.out.println("Successfully randomly assigned sections to teachers and blocks after " + numAttempts + " attempts");
        if (PRINT_INIT) {
            printSched();
        }
        List<Student> toAssign = students;
        int numAtt = 0;
        while (numAtt < STUDENT_ASSIGN_ATTEMPTS) {
            if (PRINT_ATT) {
                System.out.println("Going to try to assign " + toAssign.size() + " students: " + toAssign);
            }
            List<Student> unassignableStudents = new ArrayList<>();
            for (Student student : toAssign) {
                if (assignStudent(student) > 0) {
                    unassignableStudents.add(student);
                }
            }
            if (PRINT_ATT) {
                System.out.println(unassignableStudents.size() + " unassignable students: " + unassignableStudents);
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
        System.out.println("Schedules for students: " + temp.getStudentSchedules());
        System.out.println(temp.roster);
        result = temp;
        //result.findConflicts();
        /*
         Random r = new Random();
         for (Section section : sections) {
         if (r.nextInt(3) == 0) {
         result.timings.put(section, Block.blocks[0]);
         }
         if (r.nextInt(3) == 0) {
         result.timings.put(section, Block.blocks[1]);
         }
         //result.locations.put(section, Room.getRoomArray()[0]);
         }*/
    }
    private void printSched() {
        for (Section section : sections) {
            System.out.println(section + " at " + temp.timings.get(section) + " taught by " + temp.teachers.get(section) + " in " + temp.locations.get(section));
        }
        System.out.println("Schedules for teachers: " + temp.getTeacherSchedules());
        System.out.println("sshedules for rooms: " + temp.getRoomSchedules());
    }
    public int assignStudent(Student student) {
        Random r = new Random();
        temp.roster.reset(student);
        List<Requirement> requirements = student.getRequirements();
        int numUn = 0;
        for (Requirement requirement : requirements) {//This cannot be done in a parallel stream because canJoinClass depends on previous class joining
            Optional<Section> toJoin = requirement.getSectionOptionStream().parallel().filter(section->canJoinClass(student, section, temp)).sorted(getSectionComparator(student, temp)).findFirst();
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
    static double genderBias = 0.3;//how much it will try to put even numbers of each gender in each class.
//1: ignore class size, only minimize gender gap, 0: ignore gender gap, only equalize class sizes
    //maybe a slider on the frontend for this?
    public static Comparator<Section> getSectionComparator(Student toAdd, Schedule schedule) {
        boolean isMale = toAdd.gender == Gender.MAIL;
        return Comparator.comparingDouble(section->{
            int numMale = schedule.roster.numMale(section);
            int numStudents = schedule.roster.numStudents(section);
            int numSameGender = isMale ? numMale : numStudents - numMale;
            int numDiffGender = numStudents - numSameGender;
            double genderScore = numSameGender - numDiffGender;
            return genderScore * genderBias + numStudents * (1 - genderBias);
        });
    }
    public static boolean canJoinClass(Student student, Section section, Schedule schedule) {
        //ONLY for can the student PHYSICALLY join the class
        //other things like class size and gender bias are dealt with in the sort after this filter
        Block sectionTime = schedule.timings.get(section);
        if (sectionTime == null) {
            throw new IllegalArgumentException("must assign section times before class list");
        }
        return !schedule.studentIsInClass(student, sectionTime);//if the student is nowhere else during then that block
    }
    public int assignRandomBlocksAndTeachers() {
        Random rand = new Random();
        int numSec = sections.size();
        int numRooms = Room.getRoomArray().length;
        ArrayList<Section> sectionz = new ArrayList<>(sections);
        for (int i = 0; i < numSec; i++) {
            sectionz.add(sectionz.remove(rand.nextInt(numSec)));
        }
        for (int sectionID = 0; sectionID < numSec; sectionID++) {
            Section section = sectionz.get(sectionID);
            if (!place(section, rand)) {
                return -1;
            }
        }
        return 1;
    }
    public boolean place(Section section, Random rand) {
        ArrayList<Teacher> posss = new ArrayList<>(section.getTeachers());
        if (posss.isEmpty()) {
            System.out.println(section);
            System.exit(0);
        }
        ArrayList<Room> acceptableRooms = new ArrayList<>(section.klass.acceptableRooms);
        // for (int i = 0; i < acceptableRooms.size(); i++) {//randomize
        //    acceptableRooms.add(acceptableRooms.remove(rand.nextInt(acceptableRooms.size())));
        // }
        boolean df = false;
        while (!posss.isEmpty()) {
            Teacher t = posss.remove(rand.nextInt(posss.size()));
            ArrayList<Block> workingBlocks = new ArrayList<>(t.getWorkingBlocks());//some teachers only work some times, remember? grr
            if (workingBlocks.isEmpty()) {
                System.out.println("aontehuidhntoaeui");
                System.exit(0);
                //if this continues, netbeans complains because b might not be initialized in temp.getTeacherLocation in the while condition on line 144
            }
            boolean f = false;
            while (!workingBlocks.isEmpty()) {
                //acceptableRooms.add(acceptableRooms.remove(rand.nextInt(acceptableRooms.size())));
                Block b = workingBlocks.remove(rand.nextInt(workingBlocks.size()));
                if (temp.isTeacherUnoccupied(t, b)) {
                    Optional<Room> r = acceptableRooms.parallelStream().filter(room->temp.isRoomEmpty(room, b)).findAny();
                    if (r.isPresent()) {
                        temp.teachers.put(section, t);
                        temp.timings.put(section, b);
                        temp.locations.put(section, r.get());
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public void reset() {
        for (Section section : sections) {
            temp.teachers.put(section, null);
            temp.timings.put(section, null);
            temp.locations.put(section, null);
        }
    }
}
