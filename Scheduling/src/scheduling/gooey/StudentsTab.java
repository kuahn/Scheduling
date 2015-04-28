package scheduling.gooey;
import java.awt.*;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import scheduling.Scheduling;
import scheduling.Student;
/**
 *
 * @author leijurv
 */
public class StudentsTab extends JComponent {
    JTable table;
    public StudentsTab() {
        //setLayout(new FlowLayout());
        TableModel dataModel = new AbstractTableModel() {
            public int getColumnCount() {
                return 2;
            }
            public int getRowCount() {
                return Scheduling.rd.students.size();
            }
            public Object getValueAt(int row, int col) {
                Student s = Scheduling.rd.students.get(row);
                if (col == 0) {
                    return s.toString();
                }
                return s.grade.toString();
            }
            public String getColumnName(int col) {
                return new String[] {"Name", "Grade"}[col];
            }
            public boolean isCellEditable(int row, int col) {
                return true;
            }
        };
        table = new JTable(dataModel);
        JScrollPane kush = new JScrollPane(table);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JButton button = new JButton("Add Student");
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
