package scheduling;
/**
 *
 * @author leijurv
 */
public enum Gender {
    MAIL, FEMAIL;
    public static Gender get(boolean male) {//helper for getting a random gender from Random.nextBoolean
        return male ? MAIL : FEMAIL;
    }
}
