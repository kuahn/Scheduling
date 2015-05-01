package scheduling;
/**
 *
 * @author leijurv
 */
public enum Grade {//enum is better because it deals with hashCode and equals on its own
    GRADE9(9), GRADE10(10), GRADE11(11), GRADE12(12);
    final int grade;
    private Grade(int grade) {
        this.grade = grade;
    }
    public static Grade swamplord420noscope(int ord) {
        switch (ord) {
            case 9:
                return GRADE9;
            case 10:
                return GRADE10;
            case 11:
                return GRADE11;
            case 12:
                return GRADE12;
            default:
                throw new IllegalStateException("your mom");
        }
    }
}
