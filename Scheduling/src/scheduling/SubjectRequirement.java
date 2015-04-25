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
    final String name;
    public SubjectRequirement(Subject subject, String name) {
        this.subject = subject;
        List<Section> sec = subject.klasses.stream().flatMap(klass->Stream.of(klass.sections)).collect(Collectors.toList());
        sections = new ArrayList<>(sec);
        this.name = name;
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
        return s.klass.getSubject().equals(subject);
    }
    @Override
    public int hashCode() {
        return subject.hashCode() + 5021;
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
        return name;
    }
}
