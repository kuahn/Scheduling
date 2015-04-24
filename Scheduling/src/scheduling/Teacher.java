package scheduling;
import java.util.HashMap;
import java.util.ArrayList;
/**
 *
 * @author leijurv
 */
public class Teacher {
    final String name;
    private final boolean[] workingBlocks = new boolean[Block.numBlocks];
    ArrayList<Subject> subjectsTeached;
    ArrayList<Klass> klassesTeached;
    public Teacher(String name) {
        this.name = name;
    }
    public Teacher(String name, ArrayList<Block> doesNotWork) {
        this(name);
        for (Block notWork : doesNotWork) {
            setDoesWork(notWork, false);
        }
    }
    public final boolean worksDuringBlock(Block b) {
        return workingBlocks[b.blockID];
    }
    public final boolean worksDuringBlock(int id) {
        return workingBlocks[id];
    }
    public final void setDoesWork(Block b, boolean doesWork) {
        workingBlocks[b.blockID] = doesWork;
    }
    public final void setDoesWork(int id, boolean doesWork) {
        workingBlocks[id] = doesWork;
    }
}
