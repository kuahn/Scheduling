package scheduling;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public class Section {
    final Klass klass;
    final int sectionID;
    public Section(Klass klass, int sectionID) {
        this.klass = klass;
        this.sectionID = sectionID;
    }
    public boolean isIn(Klass klass) {
        return klass.equals(this.klass);
    }
    public boolean isIn(Subject subject) {
        return klass.getSubject().equals(subject);
    }
    public boolean conflictsWith(Section other) {
        if (equals(other)) {
            return true;//don't want to be in the same thing twice
        }
        if (klass.equals(other.klass)) {//a student cannot take two sections of the same class
            return true;
        }
        //a student CAN theoretically take two sections of the same subject
        return false;
    }
    public final char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G'};
    @Override
    public String toString() {
        return klass + " Section " + alphabet[sectionID];
    }
    public ArrayList<Teacher> getTeachers() {
        return klass.getTeachers();
    }
}
