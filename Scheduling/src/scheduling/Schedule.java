package scheduling;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 *
 * @author leijurv
 */
public class Schedule {
    HashMap<Section, Room> locations;
    HashMap<Section, Block> timings;
    HashMap<Section, Teacher> teachers;
    ArrayList<Section> sections;
    Roster roster;
    public void findConflicts() {
    }
    public List<Section> getTeacherLocation(Teacher teacher, Block time) {//well, the teacher SHOULD only be in one place...
        //this can be parallel because hashmaps are thread safe if the only operations happening are read only
        return sections.stream().parallel().filter(section->timings.get(section).equals(time) && teachers.get(section).equals(teacher)).collect(Collectors.toList());
    }
    public List<Section> getRoomUsage(Room room, Block time) {//well, there SHOULD only be one section in each room at once...
        return sections.stream().parallel().filter(section->timings.get(section).equals(time) && locations.get(section).equals(room)).collect(Collectors.toList());
    }
    public Stream<Section> getStudentSectionStream(Student student, Block time) {//well, the student SHOULD only be in one section at once..
        return roster.getSections(student).stream().parallel().filter(section->timings.get(section).equals(time));
    }
    public List<Section> getStudentSection(Student student, Block time) {
        return getStudentSectionStream(student, time).collect(Collectors.toList());
    }
    public List<Room> getStudentLocation(Student student, Block time) {//well, the student SHOULD only be in one room at once...
        return getStudentSectionStream(student, time).map(section->locations.get(section)).collect(Collectors.toList());
    }
}
