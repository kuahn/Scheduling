package scheduling;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
/**
 *
 * @author leijurv
 */
public class ResultsTab extends JComponent {
    Schedule result = Scheduling.rd.getResult();
    JTabbedPane s;
    public ResultsTab() {
        setLayout(new GridLayout(1, 1));
        s = new JTabbedPane();
        s.add(new RoomSchedules(), "By room");
        s.add(new StudentSchedules(), "By student");
        s.add(new TeacherSchedules(), "By teacher");
        add(s);
    }
    public class RoomSchedules extends JComponent {
        JTable table;
        JComboBox<Room> rooms;
        public RoomSchedules() {
            rooms = new JComboBox<>(Room.getRoomArray());
            TableModel dataModel = new AbstractTableModel() {
                public int getColumnCount() {
                    return 3;
                }
                public int getRowCount() {
                    return Block.numBlocks;
                }
                public Object getValueAt(int row, int col) {
                    if (col == 0) {
                        return "Block " + row;
                    }
                    List<Section> res = result.getRoomUsage((Room) rooms.getSelectedItem(), Block.blocks[row]);
                    if (res.isEmpty()) {
                        return "free";
                    }
                    Section sec = res.get(0);
                    if (col == 1) {
                        return sec.toString();
                    }
                    return result.teachers.get(sec).toString();
                }
                public String getColumnName(int col) {
                    return new String[] {"Block", "Section", "Teacher"}[col];
                }
            };
            table = new JTable(dataModel);
            table.getColumnModel().getColumn(1).setMinWidth(250);
            JScrollPane scrollpane = new JScrollPane(table);
            rooms.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    scrollpane.repaint();
                }
            });
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(10, 0, 0, 0);
            add(rooms, c);
            c.insets = new Insets(10, 0, 15, 0);
            c.fill = GridBagConstraints.BOTH;
            c.weighty = 1.0;
            c.gridx = 0;
            c.gridwidth = 1;
            c.gridy = 1;
            add(scrollpane, c);
        }
    }
    public class StudentSchedules extends JComponent {
        JTable table;
        JComboBox<Student> students;
        public StudentSchedules() {
            students = new JComboBox<>(Scheduling.students.toArray(new Student[400]));
            TableModel dataModel = new AbstractTableModel() {
                public int getColumnCount() {
                    return 4;
                }
                public int getRowCount() {
                    return Block.numBlocks;
                }
                public Object getValueAt(int row, int col) {
                    if (col == 0) {
                        return "Block " + row;
                    }
                    List<Section> res = result.getStudentSection((Student) students.getSelectedItem(), Block.blocks[row]);
                    if (res.isEmpty()) {
                        return "free";
                    }
                    Section sec = res.get(0);
                    if (col == 1) {
                        return sec.toString();
                    }
                    if (col == 3) {
                        return result.locations.get(sec).toString();
                    }
                    return result.teachers.get(sec).toString();
                }
                public String getColumnName(int col) {
                    return new String[] {"Block", "Section", "Teacher", "Room"}[col];
                }
            };
            table = new JTable(dataModel);
            table.getColumnModel().getColumn(1).setMinWidth(250);
            JScrollPane scrollpane = new JScrollPane(table);
            students.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    scrollpane.repaint();
                }
            });
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(10, 0, 0, 0);
            add(students, c);
            c.insets = new Insets(10, 0, 15, 0);
            c.fill = GridBagConstraints.BOTH;
            c.weighty = 1.0;
            c.gridx = 0;
            c.gridwidth = 1;
            c.gridy = 1;
            add(scrollpane, c);
        }
    }
    public class TeacherSchedules extends JComponent {
        JTable table;
        JComboBox<Teacher> teachers;
        public TeacherSchedules() {
            teachers = new JComboBox<>(Scheduling.rd.teachers.toArray(new Teacher[400]));
            TableModel dataModel = new AbstractTableModel() {
                public int getColumnCount() {
                    return 3;
                }
                public int getRowCount() {
                    return Block.numBlocks;
                }
                public Object getValueAt(int row, int col) {
                    if (col == 0) {
                        return "Block " + row;
                    }
                    List<Section> res = result.getTeacherLocation((Teacher) teachers.getSelectedItem(), Block.blocks[row]);
                    if (res.isEmpty()) {
                        return "free";
                    }
                    Section sec = res.get(0);
                    if (col == 1) {
                        return sec.toString();
                    }
                    return result.locations.get(sec).toString();
                }
                public String getColumnName(int col) {
                    return new String[] {"Block", "Section", "Room"}[col];
                }
            };
            table = new JTable(dataModel);
            //table.getColumnModel().getColumn(0).setMinWidth(50);
            //table.getColumnModel().getColumn(0).setPreferredWidth(50);
            //table.getColumnModel().getColumn(0).setMaxWidth(70);
            //table.getColumnModel().getColumn(2).setMinWidth(70);
            //table.getColumnModel().getColumn(2).setPreferredWidth(70);
            //table.getColumnModel().getColumn(2).setMaxWidth(100);
            table.getColumnModel().getColumn(1).setMinWidth(250);
            JScrollPane scrollpane = new JScrollPane(table);
            teachers.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    table.repaint();
                }
            });
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.anchor = GridBagConstraints.CENTER;
            c.insets = new Insets(10, 0, 0, 0);
            add(teachers, c);
            c.insets = new Insets(10, 0, 15, 0);
            c.fill = GridBagConstraints.BOTH;
            c.weighty = 1.0;
            c.gridx = 0;
            c.gridwidth = 1;
            c.gridy = 1;
            add(scrollpane, c);
        }
    }
}
