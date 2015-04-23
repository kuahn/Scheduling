package scheduling;
/**
 *
 * @author leijurv
 */
public class Grade {
    final int grade;
    public Grade(int grade) {
        this.grade = grade;
    }
    public int hashCode() {
        return grade * 8723;
    }
}
