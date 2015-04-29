package scheduling;
import java.util.ArrayList;
import java.util.Arrays;
/**
 *
 * @author leijurv
 */
public class Subject {
    final String name;
    final ArrayList<Klass> klasses;
    public final ArrayList<Teacher> teachers;
    public Subject(String name, ArrayList<Klass> klasses, Teacher[] teachers) {
        this.teachers = new ArrayList<>(Arrays.asList(teachers));
        this.klasses = klasses;
        this.name = name;
        registerKlasses();
    }
    public Subject(String name, String[] klassNames, int[] sectionNumbers, Teacher[] teachers) {
        this.teachers = new ArrayList<>(Arrays.asList(teachers));
        if (klassNames.length != sectionNumbers.length) {
            throw new IllegalArgumentException("Different numbers of klassNames and sectionNumbers");
        }
        this.name = name;
        int numKlasses = klassNames.length;
        klasses = new ArrayList<>(numKlasses);
        for (int i = 0; i < numKlasses; i++) {
            klasses.add(new Klass(klassNames[i], sectionNumbers[i]));
        }
        this.teachers.parallelStream().forEach((t)->{
            t.subjectsTeached.add(this);
        });
        registerKlasses();
    }
    public final void registerKlasses() {
        klasses.stream().parallel().forEach((klass)->{//This is thread safe because ArrayList.contains is thread safe
            klass.registerSubject(this);
        });
    }
    public boolean hasKlass(Klass klass) {
        return klasses.contains(klass);
    }
    @Override
    public String toString() {
        return name;
    }
}
