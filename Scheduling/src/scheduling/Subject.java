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
        registerKlasses();
    }
    public Subject(String name, String[] klassNames, int[] sectionNumbers) {
        if (klassNames.length != sectionNumbers.length) {
            throw new IllegalArgumentException("Different numbers of klassNames and sectionNumbers");
        }
        this.name = name;
        int numKlasses = klassNames.length;
        klasses = new ArrayList<>(numKlasses);
        for (int i = 0; i < numKlasses; i++) {
            klasses.add(new Klass(klassNames[i], sectionNumbers[i]));
        }
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
