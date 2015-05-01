package scheduling;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 *
 * @author leijurv
 */
public class Teacher {
    final String name;
    final String firstName;
    final String lastName;
    public final String nuevaUsername;
    private final boolean[] workingBlocks = new boolean[Block.numBlocks];
    private final ArrayList<Block> workBlocks;
    final ArrayList<Subject> subjectsTeached;
    final ArrayList<Klass> klassesTeached;
    public void write(DataOutputStream output) throws IOException {
        output.writeUTF(name);
        for (int i = 0; i < workingBlocks.length; i++) {
            output.writeBoolean(workingBlocks[i]);
        }
    }
    public static Teacher read(DataInputStream input) throws IOException {
        String name = input.readUTF();
        Teacher teacher = new Teacher(name);
        for (int i = 0; i < Block.numBlocks; i++) {
            teacher.setDoesWork(i, input.readBoolean());
        }
        return teacher;
    }
    public Teacher(String name) {
        this.name = name;
        int l = name.indexOf(' ');
        if (l != -1) {
            firstName = name.substring(0, l);
            lastName = name.substring(l + 1, name.length());
        } else {
            firstName = name;
            lastName = "";
        }
        String cut = lastName.split(" ")[0];//elllie van der rijn goes to elivan not elivand
        nuevaUsername = (firstName.charAt(0) + cut).toLowerCase();
        workBlocks = new ArrayList<>(Block.numBlocks);//ensure capacity
        for (int i = workingBlocks.length - 1; i >= 0; i--) {
            workingBlocks[i] = true;
            workBlocks.add(Block.blocks[i]);
        }
        subjectsTeached = new ArrayList<>();
        klassesTeached = new ArrayList<>();
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
    public String getinfo(Schedule s) {//used for api
        Map<Block, List<Section>> schedule = s.getTeacherSchedule(this);
        StringBuilder resp = new StringBuilder();
        resp.append("{\n");
        aq(resp, "firstname");
        resp.append(':');
        aq(resp, firstName);
        resp.append(',');
        resp.append('\n');
        aq(resp, "lastname");
        resp.append(':');
        aq(resp, lastName);
        resp.append(',');
        resp.append('\n');
        aq(resp, "username");
        resp.append(':');
        aq(resp, nuevaUsername);
        resp.append(',');
        resp.append('\n');
        aq(resp, "worksDuring");
        resp.append(":{\n");
        for (Block b : Block.blocks) {
            aq(resp, b.toString());
            resp.append(':');
            resp.append(worksDuringBlock(b));
            resp.append(",\n");
        }
        resp.append("},");
        resp.append('\n');
        aq(resp, "klassesTought");
        resp.append(":[");
        for (int i = 0; i < klassesTeached.size(); i++) {
            aq(resp, klassesTeached.get(i) + "");
            if (i != klassesTeached.size() - 1) {
                resp.append(',');
            }
        }
        resp.append("],\n");
        aq(resp, "subjectsTought");
        resp.append(":[");
        for (int i = 0; i < subjectsTeached.size(); i++) {
            aq(resp, subjectsTeached.get(i) + "");
            if (i != subjectsTeached.size() - 1) {
                resp.append(',');
            }
        }
        resp.append("],\n");
        aq(resp, "schedule");
        resp.append(":{\n");
        for (Block b : Block.blocks) {
            aq(resp, b.toString());
            resp.append(':');
            List<Section> location = schedule.get(b);
            if (location == null) {
                location = new ArrayList<>();
            }
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
