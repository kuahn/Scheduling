package scheduling.gooey;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import scheduling.Scheduling;
/**
 *
 * @author leijurv
 */
public class InfoTab extends JComponent {
    JLabel A;
    JLabel B;
    JLabel C;
    JLabel D;
    JButton startstop;
    public InfoTab() {
        A = new JLabel("");
        B = new JLabel("");
        C = new JLabel("");
        D = new JLabel("");
        startstop = new JButton("start");
        startstop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread() {
                    @Override
                    public void run() {
                        if (Scheduling.running) {
                            Scheduling.running = false;
                        } else {
                            Scheduling.start();
                        }
                    }
                }.start();
            }
        });
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.VERTICAL;
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_START;
        add(A, c);
        c.gridy = 2;
        add(B, c);
        c.gridy = 3;
        add(C, c);
        c.gridy = 4;
        add(D, c);
        c.gridy = 0;
        c.weightx = 0.5;
        c.gridx = 0;
        add(startstop, c);
        c.gridx = 0;
        c.insets = new Insets(30, 30, 30, 30);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.gridx = 2;
        c.gridwidth = 1;
        c.gridy = 5;
        add(new JComponent() {
        }, c);//spacer
    }
    public void paintComponent(Graphics g) {
        setLabels();
        super.paintComponent(g);
    }
    public static String toPercent(double v) {
        return Math.floor(v * 10000) / 100 + "%";
    }
    public void setLabels() {
        if (Scheduling.rd.isFinished()) {
            A.setText("Finished");
            remove(startstop);
            B.setText("");
            C.setText("");
            D.setText("");
            return;
        }
        boolean running = Scheduling.running;
        if (running) {
            long currTime = System.currentTimeMillis();
            long diff = currTime - Scheduling.time;
            double speed = diff;
            speed = Scheduling.numAt / speed;
            speed = speed * 1000;
            double avgNumUnplac = Math.floor(Scheduling.numU / (Scheduling.numAt) * 100) / 100;
            String avgPercentUnplac = toPercent(Scheduling.numU / (Scheduling.numAt * Scheduling.numStud));
            String a = ("On average, " + avgNumUnplac + " of " + Scheduling.numStud + ", or " + avgPercentUnplac + " of students, are unplacable. (" + Scheduling.numAt + " attempts so far)");
            String b = (diff / 1000 + " seconds since start, on average " + speed + " guesses/sec");
            String c = ("Average number of room&teacher assignment guesses required for solution: " + Scheduling.rd.average);
            String d = ("Maximum number of assignment guesses required for solution:" + Scheduling.rd.max);
            A.setText(a);
            B.setText(b);
            C.setText(c);
            D.setText(d);
            startstop.setText("stop");
        } else {
            startstop.setText("start");
            A.setText("Not running, no data");
            B.setText("\"");
            C.setText("\"");
            D.setText("\"");
        }
    }
}
