package scheduling;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 *
 * @author leijurv
 */
public class SubjectRequirement extends Requirement {
    final Subject subject;
    final ArrayList<Section> sections;
    public SubjectRequirement(Subject subject) {
        this.subject = subject;
        List<Section> sec = subject.klasses.stream().flatMap(klass->Stream.of(klass.sections)).collect(Collectors.toList());
        sections = new ArrayList<>(sec);
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
        return s.klass.getSubject().equals(subject);
    }
}
