package scheduling;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
/**
 *
 * @author leijurv
 */
public class RoomsTab extends JComponent {
    JTable table;
    public RoomsTab() {
        //setLayout(new FlowLayout());
        TableModel dataModel = new AbstractTableModel() {
            public int getColumnCount() {
                return 1;
            }
            public int getRowCount() {
                return Room.numRooms;
            }
            public Object getValueAt(int row, int col) {
                return Room.getRoomArray()[row];
            }
            public String getColumnName(int col) {
                return new String[] {"Room"}[col];
            }
        };
        table = new JTable(dataModel);
        JScrollPane kush = new JScrollPane(table);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JButton button = new JButton("Add Room");
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
