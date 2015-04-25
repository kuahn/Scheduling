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
    public Stream<Section> getSectionStream() {
        return sections.stream();
    }
    @Override
    public ArrayList<Section> getSectionList() {
        return sections;
    }

    @Override
    public boolean fufilledBy(Section s) {
        if (s == null) {
            return false;
        }
        return s.klass.equals(klass);
    }
}
