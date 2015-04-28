package scheduling.gooey;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import scheduling.Scheduling;
import scheduling.Teacher;
/**
 *
 * @author leijurv
 */
public class TeachersTab extends JComponent {
    JTable table;
    public TeachersTab() {
        //setLayout(new FlowLayout());
        TableModel dataModel = new AbstractTableModel() {
            public int getColumnCount() {
                return 2;
            }
            public int getRowCount() {
                return Scheduling.rd.teachers.size();
            }
            public Object getValueAt(int row, int col) {
                Teacher s = Scheduling.rd.teachers.get(row);
                if (col == 0) {
                    return s.toString();
                }
                return s.getWorkingBlocks().toString();
            }
            public String getColumnName(int col) {
                return new String[] {"Name", "Works During"}[col];
            }
        };
        table = new JTable(dataModel);
        JScrollPane kush = new JScrollPane(table);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JButton button = new JButton("Add Teacher");
        c.gridx = 0;
        c.anchor = GridBagConstraints.CENTER;
        add(button, c);
        button = new JButton("Button 2");
        c.weightx = 0.5;
        c.gridx = 1;
        add(button, c);
        button = new JButton("Button 3");
        c.gridx = 2;
        add(button, c);
        c.insets = new Insets(30, 30, 30, 30);
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridwidth = 3;
        c.gridy = 1;
        add(kush, c);
    }
}
