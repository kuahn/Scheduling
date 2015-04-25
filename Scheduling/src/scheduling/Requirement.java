package scheduling;
import java.util.ArrayList;
import java.util.stream.Stream;
/**
 *
 * @author leijurv
 */
public abstract class Requirement {
    public abstract Stream<Section> getSectionStream();
    public abstract ArrayList<Section> getSectionList();
    public abstract boolean fufilledBy(Section s);//we COULd have it just do "return getSectionList.contains(s);", but it can be faster
}
