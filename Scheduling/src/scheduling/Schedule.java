package scheduling;
import java.util.HashMap;
/**
 *
 * @author leijurv
 */
public class Schedule extends Thread {
    HashMap<Section, Room> locations;
    HashMap<Section, Block> timings;
    HashMap<Section, Teacher> teachers;
    Roster roster;
}
