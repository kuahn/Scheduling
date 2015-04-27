package scheduling;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
/**
 *
 * @author leijurv
 */
public class Gooey {
    static JTabbedPane tabs = new JTabbedPane();
    static InfoTab infotab;
    static StudentsTab studentstab;
    static ResultsTab results;
    public static void setupTabs() {
        infotab = new InfoTab();
        studentstab = new StudentsTab();
        tabs.add(studentstab, "Students");
        tabs.add(infotab, "Info");
    }
    public static void setup() {
        JFrame frame = new JFrame("naoeidun");
        //frame.setLayout(new FlowLayout());
        setupTabs();
        frame.setContentPane(tabs);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(2000, 2000);
        frame.setVisible(true);
    }
    public static void onFinish() {
        tabs.remove(infotab);
        results = new ResultsTab();
        tabs.add(results, "Results");
    }
}
