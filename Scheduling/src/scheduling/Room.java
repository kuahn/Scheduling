package scheduling;
import java.util.ArrayList;
import java.util.Optional;
/**
 *
 * @author leijurv
 */
public class Room {
    private static final int[] roomNumbers = new int[18];
    public static final int numRooms = roomNumbers.length;
    private static final Room[] roomArray = new Room[numRooms];
    private static final ArrayList<Room> rooms = new ArrayList<>(numRooms);//ensure capacity
    static{
        for (int i = 0; i < numRooms; i++) {
            roomNumbers[i] = 100 + i;
        }
        for (int i = 0; i < numRooms; i++) {
            Room r = new Room(roomNumbers[i]);
            roomArray[i] = r;
            rooms.add(r);
        }
    }
    final int roomNumber;
    private Room(int roomNumber) {
        this.roomNumber = roomNumber;
    }
    public static Room getRoom(int roomNumber) {
        Optional<Room> matches = rooms.parallelStream().filter(room->room.roomNumber == roomNumber).findAny();
        if (matches.isPresent()) {
            return matches.get();
        }
        return null;
    }
    public static ArrayList<Room> getRooms() {
        return rooms;
    }
    public static Room[] getRoomArray() {
        return roomArray;
    }
    @Override
    public String toString() {
        return "Room " + roomNumber;
    }
}
