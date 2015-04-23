package scheduling;
/**
 *
 * @author leijurv
 */
public class Block {
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
