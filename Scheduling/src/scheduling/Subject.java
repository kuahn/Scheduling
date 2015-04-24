package scheduling;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public class Subject {
    final String name;
    final ArrayList<Klass> klasses;
    public Subject(String name, ArrayList<Klass> klasses) {
        this.klasses = klasses;
        this.name = name;
        for (Klass klass : klasses) {
            klass.registerSubject(this);
        }
    }
    public boolean hasKlass(Klass klass) {
        return klasses.contains(klass);
    }
    public static Subject createSubject(String name, String[] klassNames, int[] sectionNumbers) {
        if (klassNames.length != sectionNumbers.length) {
            throw new IllegalArgumentException("Different numbers of klassNames and sectionNumbers");
        }
        int numKlasses = klassNames.length;
        ArrayList<Klass> klasses = new ArrayList<>(numKlasses);
        for (int i = 0; i < numKlasses; i++) {
            klasses.add(new Klass(klassNames[i], sectionNumbers[i]));
        }
        return new Subject(name, klasses);
    }
    public String toString() {
        return name;
    }
}
