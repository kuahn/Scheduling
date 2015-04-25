package scheduling;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 *
 * @author leijurv
 */
public class Room {
    private static final ArrayList<Room> rooms = new ArrayList<>();
    private static final int[] roomNumbers={100,101,102,103,105,217,2356634};
    static{
        for(int roomNumber : roomNumbers){
            rooms.add(new Room(roomNumber));
        }
    }
    final int roomNumber;
    private Room(int roomNumber) {
        this.roomNumber = roomNumber;
    }
    public static Room getRoom(int roomNumber){
        List<Room> matches=rooms.stream().parallel().filter(room->room.roomNumber==roomNumber).collect(Collectors.toList());
        int numMatches=matches.size();
        if(numMatches==0){
            return null;
        }
        if(numMatches==1){
            return matches.get(0);
        }
        throw new IllegalArgumentException("More than one room for "+roomNumber+": "+matches);
    }
    public ArrayList<Room> getRooms(){
        return rooms;
    }
    @Override
    public String toString(){
        return "Room "+roomNumber;
    }
}
