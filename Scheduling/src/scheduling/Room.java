package scheduling;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
    public String getinfo(Schedule s) {//used for api
        Map<Block, List<Section>> schedule = s.getRoomSchedule(this);
        StringBuilder resp = new StringBuilder();
        resp.append("{\n");
        aq(resp, "roomNumber");
        resp.append(':');
        resp.append(roomNumber);
        resp.append(',');
        resp.append('\n');
        aq(resp, "schedule");
        resp.append(":{\n");
        for (Block b : Block.blocks) {
            aq(resp, b.toString());
            resp.append(':');
            List<Section> location = schedule.get(b);
            List<String> res = location.parallelStream().map(section->'"' + section.toString() + '"').collect(Collectors.toList());
            resp.append(res.toString());
            resp.append(",\n");
        }
        resp.append("}}");
        return resp.toString();
    }
    public static void aq(StringBuilder r, String s) {//append something within quotes, helper for JSON creation
        // TODO use that JSON library to make JSON
        r.append("\"");
        r.append(s);
        r.append("\"");
    }
}
