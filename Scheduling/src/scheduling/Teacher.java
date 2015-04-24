package scheduling;
import java.util.HashMap;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public class Teacher {
    final String name;
    final HashMap<Block, Boolean> workingTimes;
    public Teacher(String name, HashMap<Block, Boolean> workingTimes) {
        this.name = name;
        this.workingTimes = workingTimes;
    }
    public Teacher(String name) {
        this(name, worksAlways());
    }
    public Teacher(String name, ArrayList<Block> works) {
        this(name, fromBlocks(works));
    }
    public static HashMap<Block, Boolean> worksAlways() {
        HashMap<Block, Boolean> result = new HashMap<>();
        for (Block b : Block.blocks) {
            result.put(b, true);
        }
        return result;
    }
    public static HashMap<Block, Boolean> fromBlocks(ArrayList<Block> blocks) {
        HashMap<Block, Boolean> result = new HashMap<>();
        for (Block b : blocks) {
            result.put(b, true);
        }
        return result;
    }
    public boolean worksDuringBlock(Block b) {
        Boolean w = workingTimes.get(b);
        if (w == null) {
            return false;
        }
        return w;
    }
}
