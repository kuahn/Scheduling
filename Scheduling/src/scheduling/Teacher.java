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
    private final ArrayList<Block> workBlocks;
    ArrayList<Subject> subjectsTeached;
    ArrayList<Klass> klassesTeached;
    public Teacher(String name) {
        this.name = name;
        workBlocks = new ArrayList<>(Block.numBlocks);//ensure capacity
        for (int i = workingBlocks.length - 1; i >= 0; i--) {
            workingBlocks[i] = true;
            workBlocks.add(Block.blocks[i]);
        }
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
        int blockID = b.blockID;
        boolean current = workingBlocks[blockID];
        workingBlocks[blockID] = doesWork;
        System.out.println(this + (doesWork ? " works" : " does not work") + " during " + b);
        if (doesWork ^ current) {//if it changed
            if (doesWork) {
                workBlocks.add(b);
            } else {
                workBlocks.remove(b);
            }
        }
    }
    public final void setDoesWork(int id, boolean doesWork) {
        boolean current = workingBlocks[id];
        workingBlocks[id] = doesWork;
        System.out.println(this + (doesWork ? " works" : " does not work") + " during Block " + id);
        if (doesWork ^ current) {//if it changed
            Block b = Block.blocks[id];
            if (doesWork) {
                workBlocks.add(b);
            } else {
                workBlocks.remove(b);
            }
        }
    }
    public ArrayList<Block> getWorkingBlocks() {
        return workBlocks;
    }
    @Override
    public String toString() {
        return name;
    }
}
