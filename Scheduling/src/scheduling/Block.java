package scheduling;
/**
 *
 * @author leijurv
 */
public class Block {
    static final int numBlocks = 7;
    static final Block[] blocks = new Block[numBlocks];
    static{
        for (int i = 0; i < numBlocks; i++) {
            blocks[i] = new Block(i, 3);// TODO not every block meets 3x/ week. Maybe a BlockSchedule class?
        }
    }
    final int blockID;
    final int meetingsPerWeek;
    public Block(int blockID, int meetingsPerWeek) {
        this.blockID = blockID;
        this.meetingsPerWeek = meetingsPerWeek;
    }
    public int hashCode() {
        return blockID;
    }
}
