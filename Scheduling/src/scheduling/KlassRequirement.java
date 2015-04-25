package scheduling;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;
/**
 *
 * @author leijurv
 */
public class KlassRequirement extends Requirement {
    final Klass klass;
    final ArrayList<Section> sections;
    public KlassRequirement(Klass klass) {
        this.klass = klass;
        sections = new ArrayList<>(Arrays.asList(klass.sections));
    }
    @Override
    public Stream<Section> getSectionOptionStream() {
        return sections.stream();
    }
    @Override
    public ArrayList<Section> getSectionOptionsList() {
        return sections;
    }
    @Override
    public boolean fufilledBy(Section s) {
        if (s == null) {
            return false;
        }
        return s.klass.equals(klass);
    }
    @Override
    public int hashCode() {
        return klass.hashCode() + 50215021;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return hashCode() == obj.hashCode();
    }
    @Override
    public String toString() {
        return klass + " requirement";
    }
}
