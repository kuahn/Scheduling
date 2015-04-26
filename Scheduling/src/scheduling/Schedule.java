package scheduling;
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
}
