package scheduling;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public class Klass {
    private Subject subject = null;
    final String name;
    final int numSections;
    final ArrayList<Section> sections;
    public Klass(String name, int numSections) {
        this.name = name;
        this.numSections = numSections;
        sections = new ArrayList<>(numSections);//ensure capacity
        for (int i = 0; i < numSections; i++) {
            sections.add(new Section(this, i));
        }
    }
    public void registerSubject(Subject s) {
        if (subject != null) {
            throw new IllegalStateException("Already has subject");
        }
        if (!s.hasKlass(this)) {
            throw new IllegalStateException("YOU ARE NOT MY REAL SUBJECT");
        }
        subject = s;
    }
    public Subject getSubject() {
        return subject;
    }
    public String toString() {
        return subject + "--" + name;
    }
}
