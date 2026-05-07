package project1.admin;

import project1.ui.UITheme; // ✅ Import the theme utility

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class NurseRecordsFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public NurseRecordsFrame() {
        setTitle("Nurse Records");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table setup
        String[] columns = {"Nurse ID", "User ID", "Name", "Specialization", "Availability", "Phone", "Email"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        UITheme.styleTable(table); // ✅ Apply theme
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton searchBtn = new JButton("Search");

        // ✅ Apply themed colors
        UITheme.styleButton(addBtn, new Color(46, 204, 113), Color.WHITE);   // Green
        UITheme.styleButton(updateBtn, new Color(52, 152, 219), Color.WHITE); // Blue
        UITheme.styleButton(deleteBtn, new Color(231, 76, 60), Color.WHITE);  // Red
        UITheme.styleButton(searchBtn, new Color(241, 196, 15), Color.BLACK); // Yellow

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(searchBtn);
        UITheme.stylePanel(buttonPanel); // ✅ Apply panel theme

        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        addBtn.addActionListener(e -> addNurse());
        updateBtn.addActionListener(e -> updateNurse());
        deleteBtn.addActionListener(e -> deleteNurse());
        searchBtn.addActionListener(e -> searchNurse());

        loadNurses();
        setVisible(true);
    }

    // ✅ Load all nurses into JTable
    private void loadNurses() {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Nurses")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("nurse_id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getBoolean("availability"),
                        rs.getString("phone"),
                        rs.getString("email")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading nurses: " + ex.getMessage());
        }
    }

    // ✅ Add nurse
    private void addNurse() {
        String name = JOptionPane.showInputDialog(this, "Enter Nurse Name:");
        if (name == null || name.trim().isEmpty()) return;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO Nurses(user_id, name, specialization, availability, phone, email) VALUES (?,?,?,?,?,?)")) {
            ps.setInt(1, 0); // or generate user_id
            ps.setString(2, name);
            ps.setString(3, "General");
            ps.setBoolean(4, true);
            ps.setString(5, "0000000000");
            ps.setString(6, name.toLowerCase() + "@hospital.com");
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Nurse added successfully!");
            loadNurses();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding nurse: " + ex.getMessage());
        }
    }

    // ✅ Update nurse
    private void updateNurse() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a nurse to update!");
            return;
        }
        int nurseId = (int) model.getValueAt(row, 0);
        String newPhone = JOptionPane.showInputDialog(this, "Enter new phone number:");
        if (newPhone == null || newPhone.trim().isEmpty()) return;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
             PreparedStatement ps = conn.prepareStatement("UPDATE Nurses SET phone=? WHERE nurse_id=?")) {
            ps.setString(1, newPhone);
            ps.setInt(2, nurseId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Nurse updated successfully!");
            loadNurses();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating nurse: " + ex.getMessage());
        }
    }

    // ✅ Delete nurse
    private void deleteNurse() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a nurse to delete!");
            return;
        }
        int nurseId = (int) model.getValueAt(row, 0);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Nurses WHERE nurse_id=?")) {
            ps.setInt(1, nurseId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Nurse deleted successfully!");
            loadNurses();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting nurse: " + ex.getMessage());
        }
    }

    // ✅ Search nurse
    private void searchNurse() {
        String name = JOptionPane.showInputDialog(this, "Enter Nurse Name to search:");
        if (name == null || name.trim().isEmpty()) return;

        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Nurses WHERE name LIKE ?")) {
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("nurse_id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getBoolean("availability"),
                        rs.getString("phone"),
                        rs.getString("email")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error searching nurse: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NurseRecordsFrame::new);
    }
}