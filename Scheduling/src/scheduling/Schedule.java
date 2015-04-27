package scheduling;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 *
 * @author leijurv
 */
public class Schedule {
    final HashMap<Section, Room> locations;
    final HashMap<Section, Block> timings;
    final HashMap<Section, Teacher> teachers;
    final ArrayList<Section> sections;
    final ArrayList<Student> students;
    final Roster roster;
    public Schedule(ArrayList<Section> sections, ArrayList<Student> students) {
        locations = new HashMap<>();
        timings = new HashMap<>();
        teachers = new HashMap<>();
        this.sections = sections;
        this.students = students;
        this.roster = new Roster(students, sections);
    }
    public boolean verifyRoomsTeachers(ArrayList<Teacher> teacherz) {
        for (Block block : Block.blocks) {
            if (!Room.getRooms().parallelStream().noneMatch((room)->(getRoomUsage(room, block).size() > 1))) {
                return false;
            }
            if (!teacherz.parallelStream().noneMatch((t)->(getTeacherLocation(t, block).size() > 1))) {
                return false;
            }
        }
        return true;
    }
    public List<Section> getTeacherLocation(Teacher teacher, Block time) {//well, the teacher SHOULD only be in one place...
        //this can be parallel because hashmaps are thread safe if the only operations happening are read only
        return sections.parallelStream().filter(section->time.equals(timings.get(section)) && teacher.equals(teachers.get(section))).collect(Collectors.toList());
    }
    public List<Section> getRoomUsage(Room room, Block time) {//well, there SHOULD only be one section in each room at once...
        return sections.parallelStream().filter(section->time.equals(timings.get(section)) && room.equals(locations.get(section))).collect(Collectors.toList());
    }
    public Stream<Section> getStudentSectionStream(Student student, Block time) {//well, the student SHOULD only be in one section at once..
        return roster.getSections(student).parallelStream().filter(section->time.equals(timings.get(section)));
    }
    public List<Section> getStudentSection(Student student, Block time) {
        return getStudentSectionStream(student, time).collect(Collectors.toList());
    }
    public List<Room> getStudentLocation(Student student, Block time) {//well, the student SHOULD only be in one room at once...
        return getStudentSectionStream(student, time).map(section->locations.get(section)).collect(Collectors.toList());
    }
    public Map<Block, Section> getSchedule(Predicate<Section> pred) {
        return sections.parallelStream()
                .filter(pred)//get the sections that this teacher is teaching
                .collect(Collectors.toMap(section->timings.get(section), section->section));//map of timings.get(section) to section
    }
    public Map<Block, Section> getTeacherSchedule(Teacher teacher) {
        return getSchedule(section->teacher.equals(teachers.get(section)));
    }
    public Map<Block, Section> getRoomSchedule(Room room) {
        return getSchedule(section->room.equals(locations.get(section)));
    }
    public Map<Block, Section> getStudentSchedule(Student student) {
        return roster.getSections(student).parallelStream().collect(Collectors.toMap(section->timings.get(section), section->section));
    }
    public Map<Teacher, Map<Block, Section>> getTeacherSchedules(List<Teacher> teacherz) {
        return teacherz.parallelStream().collect(Collectors.toMap(teacher->teacher, teacher->getTeacherSchedule(teacher)));
    }
    public Map<Student, Map<Block, Section>> getStudentSchedules() {
        return students.parallelStream().collect(Collectors.toMap(student->student, student->getStudentSchedule(student)));
    }
    public Map<Room, Map<Block, Section>> getRoomSchedules() {
        return Room.getRooms().parallelStream().collect(Collectors.toMap(room->room, room->getRoomSchedule(room)));
    }
    public static void output(Scheduler rd) throws IOException {
        Schedule schedule = rd.getResult();
        Map<Teacher, Map<Block, Section>> teacherSchedules = schedule.getTeacherSchedules(rd.teachers);
        Map<Room, Map<Block, Section>> roomSchedules = schedule.getRoomSchedules();
        Map<Student, Map<Block, Section>> studentSchedules = schedule.getStudentSchedules();
        String basePath = System.getProperty("user.home") + "/Documents/schedout/";
        File base = new File(basePath);
        File main = new File(basePath + "sections.csv");
        schedule.roster.write(new File(basePath + "roster"));
        try(FileWriter writer = new FileWriter(main)) {
            writer.write("Section,Block,Teacher,Room\n");
            for (Section section : schedule.sections) {
                writer.write(section + "," + schedule.timings.get(section).blockID + "," + schedule.teachers.get(section) + "," + schedule.locations.get(section).roomNumber + "\n");
            }
        }
        new File(basePath + "teachers").mkdir();
        new File(basePath + "students").mkdir();
        new File(basePath + "rooms").mkdir();
        for (Teacher teacher : rd.teachers) {
            write(base, teacherSchedules.get(teacher), schedule, "teachers", teacher.toString());
        }
        for (Student student : rd.students) {
            write(base, studentSchedules.get(student), schedule, "students", student.toString());
        }
        for (Room room : Room.getRoomArray()) {
            write(base, roomSchedules.get(room), schedule, "rooms", room.toString());
        }
    }
    public static void write(File main, Map<Block, Section> data, Schedule schedule, String folderName, String thisName) throws IOException {
        main = new File(main.toString() + File.separatorChar + folderName + File.separatorChar + thisName + ".csv");
        try(FileWriter writer = new FileWriter(main)) {
            writer.write("Section,Block,Teacher,Room\n");
            for (Block b : data.keySet()) {
                Section section = data.get(b);
                writer.write(section + "," + schedule.timings.get(section).blockID + "," + schedule.teachers.get(section) + "," + schedule.locations.get(section).roomNumber + "\n");
            }
        }
    }
}
