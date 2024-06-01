import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Main {
    private static final String HOSTNAME = "5pz.h.filess.io";
    private static final String DATABASE = "ProjectPemlan_rootsilent";
    private static final String PORT = "3307";
    private static final String USERNAME = "ProjectPemlan_rootsilent";
    private static final String PASSWORD = "a10b611514e82b41bac39bd71deeef6d15965c98";

    private static MySQL mysql;
    private static Connection conn;
    private static DefaultTableModel tableModel;

    public static void main(String[] args) {
        mysql = new MySQL(HOSTNAME, DATABASE, PORT, USERNAME, PASSWORD);
        try {
            conn = mysql.connect();
            System.out.println("Connected: " + !conn.isClosed());

            JFrame frame = new JFrame("Employee Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            // Load the application icon
            ImageIcon appIcon = new ImageIcon(Main.class.getResource("/app_icon.png"));
            frame.setIconImage(appIcon.getImage());

            JMenuBar menuBar = new JMenuBar();
            JMenu helpMenu = new JMenu("Help");
            JMenuItem helpItem = new JMenuItem("How to Use");
            helpMenu.add(helpItem);
            menuBar.add(helpMenu);
            frame.setJMenuBar(menuBar);

            JPanel panel = new JPanel();
            frame.add(panel);
            placeComponents(panel);

            helpItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showHelpDialog();
                }
            });

            frame.setVisible(true);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setBounds(10, 20, 80, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20);
        nameText.setBounds(100, 20, 165, 25);
        panel.add(nameText);

        JLabel positionLabel = new JLabel("Position");
        positionLabel.setBounds(10, 50, 80, 25);
        panel.add(positionLabel);

        JTextField positionText = new JTextField(20);
        positionText.setBounds(100, 50, 165, 25);
        panel.add(positionText);

        JLabel salaryLabel = new JLabel("Salary");
        salaryLabel.setBounds(10, 80, 80, 25);
        panel.add(salaryLabel);

        JTextField salaryText = new JTextField(20);
        salaryText.setBounds(100, 80, 165, 25);
        panel.add(salaryText);

        JLabel dateJoinedLabel = new JLabel("Date Joined");
        dateJoinedLabel.setBounds(10, 110, 80, 25);
        panel.add(dateJoinedLabel);

        JTextField dateJoinedText = new JTextField(20);
        dateJoinedText.setBounds(100, 110, 165, 25);
        panel.add(dateJoinedText);

        JButton addButton = new JButton("Add");
        addButton.setBounds(10, 140, 80, 25);
        panel.add(addButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(100, 140, 80, 25);
        panel.add(deleteButton);

        JButton editButton = new JButton("Edit");
        editButton.setBounds(190, 140, 80, 25);
        panel.add(editButton);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Position", "Salary", "Date Joined"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 170, 760, 380);
        panel.add(scrollPane);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameText.getText();
                String position = positionText.getText();
                String salary = salaryText.getText();
                String dateJoined = dateJoinedText.getText();
                if (name.isEmpty() || position.isEmpty() || salary.isEmpty() || dateJoined.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                addData(name, position, salary, dateJoined);
                nameText.setText("");
                positionText.setText("");
                salaryText.setText("");
                dateJoinedText.setText("");
                refreshTable();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) tableModel.getValueAt(selectedRow, 0);
                    deleteData(id);
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(panel, "Please select a row to delete", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int id = (int) tableModel.getValueAt(selectedRow, 0);
                    String name = (String) tableModel.getValueAt(selectedRow, 1);
                    String position = (String) tableModel.getValueAt(selectedRow, 2);
                    String salary = (String) tableModel.getValueAt(selectedRow, 3);
                    String dateJoined = (String) tableModel.getValueAt(selectedRow, 4);

                    nameText.setText(name);
                    positionText.setText(position);
                    salaryText.setText(salary);
                    dateJoinedText.setText(dateJoined);

                    int result = JOptionPane.showConfirmDialog(panel, "Update the selected row?", "Confirm Update", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        updateData(id, nameText.getText(), positionText.getText(), salaryText.getText(), dateJoinedText.getText());
                        refreshTable();
                    }
                } else {
                    JOptionPane.showMessageDialog(panel, "Please select a row to edit", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        refreshTable();
    }

    private static void addData(String name, String position, String salary, String dateJoined) {
        String sql = "INSERT INTO employees (name, position, salary, date_joined) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, position);
            pstmt.setString(3, salary);
            pstmt.setDate(4, Date.valueOf(dateJoined));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteData(int id) {
        String sql = "DELETE FROM employees WHERE employee_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateData(int id, String name, String position, String salary, String dateJoined) {
        String sql = "UPDATE employees SET name = ?, position = ?, salary = ?, date_joined = ? WHERE employee_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, position);
            pstmt.setString(3, salary);
            pstmt.setDate(4, Date.valueOf(dateJoined));
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void refreshTable() {
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM employees";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("employee_id");
                String name = rs.getString("name");
                String position = rs.getString("position");
                String salary = rs.getString("salary");
                String dateJoined = rs.getString("date_joined");
                tableModel.addRow(new Object[]{id, name, position, salary, dateJoined});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void showHelpDialog() {
        JOptionPane.showMessageDialog(null, "This is the Employee Management System.\n" +
                "1. To add an employee, fill in the fields and click 'Add'.\n" +
                "2. To delete an employee, select a row and click 'Delete'.\n" +
                "3. To edit an employee, select a row, modify the fields, and click 'Edit'.", "Help", JOptionPane.INFORMATION_MESSAGE);
    }
}
