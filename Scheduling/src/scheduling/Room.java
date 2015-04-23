package scheduling;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public class Room {
    static final ArrayList<Room> rooms = new ArrayList<>();
    static{
        //Instantiate rooms
    }
    final int roomNumber;
    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}
