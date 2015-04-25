package scheduling;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
    public void findConflicts() {
    }
    public List<Section> getTeacherLocation(Teacher teacher, Block time) {//well, the teacher SHOULD only be in one place...
        //this can be parallel because hashmaps are thread safe if the only operations happening are read only
        return sections.stream().parallel().filter(section->time.equals(timings.get(section)) && teacher.equals(teachers.get(section))).collect(Collectors.toList());
    }
    public List<Section> getRoomUsage(Room room, Block time) {//well, there SHOULD only be one section in each room at once...
        return sections.stream().parallel().filter(section->time.equals(timings.get(section)) && room.equals(locations.get(section))).collect(Collectors.toList());
    }
    public Stream<Section> getStudentSectionStream(Student student, Block time) {//well, the student SHOULD only be in one section at once..
        return roster.getSections(student).stream().parallel().filter(section->time.equals(timings.get(section)));
    }
    public List<Section> getStudentSection(Student student, Block time) {
        return getStudentSectionStream(student, time).collect(Collectors.toList());
    }
    public List<Room> getStudentLocation(Student student, Block time) {//well, the student SHOULD only be in one room at once...
        return getStudentSectionStream(student, time).map(section->locations.get(section)).collect(Collectors.toList());
    }
    public String printSchedule(Teacher teacher) {
        Map<Block, Section> result = sections.stream()
                .filter(section->teacher.equals(teachers.get(section)))//get the sections that this teacher is teaching
                .collect(Collectors.toMap(section->timings.get(section), section->section));//map of timings.get(section) to section
        return result.toString();
    }
}
