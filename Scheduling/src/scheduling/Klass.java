package scheduling;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 *
 * @author leijurv
 */
public class Klass {
    private Subject subject = null;
    final String name;
    final int numSections;
    final Section[] sections;
    final ArrayList<Teacher> teachers;
    ArrayList<Room> acceptableRooms;
    public Klass(String name, int numSections, List<Teacher> customTeachers, ArrayList<Room> acceptableRooms) {
        this.name = name;
        this.numSections = numSections;
        sections = new Section[numSections];
        for (int i = 0; i < numSections; i++) {
            sections[i] = new Section(this, i);
        }
        teachers = new ArrayList<>(customTeachers);
        teachers.stream().forEach(teacher->teacher.klassesTeached.add(this));
        this.acceptableRooms = new ArrayList<>(acceptableRooms);
    }
    public Klass(String name, int numSections, Teacher[] customTeachers, ArrayList<Room> acceptableRooms) {
        this(name, numSections, Arrays.asList(customTeachers), acceptableRooms);
    }
    public Klass(String name, int numSections, Teacher[] customTeachers) {
        this(name, numSections, customTeachers, Room.getRooms());
    }
    public Klass(String name, int numSections) {
        this(name, numSections, new Teacher[] {});
    }
    public void registerSubject(Subject s) {
        if (subject != null) {
            throw new IllegalStateException("Already has subject");
        }
        if (!s.hasKlass(this)) {
            throw new IllegalStateException("YOU ARE NOT MY REAL SUBJECT");
        }
        for (Teacher t : s.teachers) {
            if (!teachers.contains(t)) {
                teachers.add(t);
            }
        }
        subject = s;
    }
    public Subject getSubject() {
        return subject;
    }
    public void setAcceptableRoom(Room room) {
        resetAcceptableRooms();
        addAcceptableRoom(room);
    }
    public void addAcceptableRoom(Room room) {
        acceptableRooms.add(room);
    }
    public void resetAcceptableRooms() {
        acceptableRooms = new ArrayList<>();
    }
    @Override
    public String toString() {
        return subject + "--" + name;
    }
    public ArrayList<Teacher> getTeachers() {
        return teachers;
    }
    public void write(DataOutputStream output) throws IOException {
        output.writeUTF(name);
        output.writeInt(numSections);
        ArrayList<String> teacherUsernames = new ArrayList<>();
        for (Teacher t : teachers) {
            if (!subject.teachers.contains(t)) {
                teacherUsernames.add(t.name);
            }
        }
        output.writeInt(teacherUsernames.size());
        for (String kuSh : teacherUsernames) {
            output.writeUTF(kuSh);
        }
        output.writeInt(acceptableRooms.size());
        for (Room r : acceptableRooms) {
            output.writeInt(r.roomNumber);
        }
    }
    public static Klass read(DataInputStream input) throws IOException {
        String name = input.readUTF();
        int numSections = input.readInt();
        int numTeachers = input.readInt();
        ArrayList<Teacher> teachers = new ArrayList<>(numTeachers);
        for (int i = 0; i < numTeachers; i++) {
            teachers.add(Scheduling.getTeacher(input.readUTF()));
        }
        int numAcceptableRooms = input.readInt();
        ArrayList<Room> acceptableRooms = new ArrayList<>(numAcceptableRooms);
        for (int i = 0; i < numAcceptableRooms; i++) {
            acceptableRooms.add(Room.getRoom(input.readInt()));
        }
        Klass k = new Klass(name, numSections, teachers, acceptableRooms);
        System.out.println(k + "," + acceptableRooms);
        return k;
    }
}
