package scheduling;
/**
 *
 * @author leijurv
 */
public class Section {
    Klass klass;
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
}
