package project1.admin;

import project1.ui.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DoctorFrame extends JFrame {
    private JTextField idField, nameField, specializationField, contactField;
    private JTable table;
    private DefaultTableModel tableModel;

    public DoctorFrame() {
        setTitle("Doctor Management");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // --- Labels & Fields ---
        JLabel idLabel = new JLabel("Doctor ID:");
        idLabel.setBounds(30, 30, 100, 30);
        UITheme.styleLabel(idLabel);
        add(idLabel);

        idField = new JTextField();
        idField.setBounds(150, 30, 150, 30);
        add(idField);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(30, 70, 100, 30);
        UITheme.styleLabel(nameLabel);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 70, 150, 30);
        add(nameField);

        JLabel specializationLabel = new JLabel("Specialization:");
        specializationLabel.setBounds(30, 110, 100, 30);
        UITheme.styleLabel(specializationLabel);
        add(specializationLabel);

        specializationField = new JTextField();
        specializationField.setBounds(150, 110, 150, 30);
        add(specializationField);

        JLabel contactLabel = new JLabel("Contact:");
        contactLabel.setBounds(30, 150, 100, 30);
        UITheme.styleLabel(contactLabel);
        add(contactLabel);

        contactField = new JTextField();
        contactField.setBounds(150, 150, 150, 30);
        add(contactField);

        // --- Buttons ---
        JButton addButton = new JButton("Add");
        addButton.setBounds(30, 200, 100, 30);
        UITheme.styleButton(addButton, new Color(46, 204, 113), Color.WHITE); // Green
        add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(140, 200, 100, 30);
        UITheme.styleButton(updateButton, new Color(52, 152, 219), Color.WHITE); // Blue
        add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(250, 200, 100, 30);
        UITheme.styleButton(deleteButton, new Color(231, 76, 60), Color.WHITE); // Red
        add(deleteButton);

        // --- Table setup ---
        tableModel = new DefaultTableModel(new String[]{"Doctor ID", "Name", "Specialization", "Contact"}, 0);
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 250, 620, 130);
        add(scrollPane);

        // --- Load doctors from DB ---
        loadDoctors();

        // --- Button actions ---
        addButton.addActionListener(e -> addDoctor());
        updateButton.addActionListener(e -> updateDoctor());
        deleteButton.addActionListener(e -> deleteDoctor());

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hospital"; // replace with your DB name
        String user = "root"; // replace with your MySQL username
        String password = "1234"; // replace with your MySQL password
        return DriverManager.getConnection(url, user, password);
    }

    // ✅ Load doctors
    private void loadDoctors() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM doctors")) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("doctor_id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getString("contact")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading doctors: " + e.getMessage());
        }
    }

    // ✅ Add doctor
    private void addDoctor() {
        String name = nameField.getText().trim();
        String specialization = specializationField.getText().trim();
        String contact = contactField.getText().trim();

        if (name.isEmpty() || specialization.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled!");
            return;
        }

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO doctors (name, specialization, contact) VALUES (?, ?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, specialization);
            ps.setString(3, contact);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Doctor added successfully!");
            loadDoctors();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding doctor: " + ex.getMessage());
        }
    }

    // ✅ Update doctor
    private void updateDoctor() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a doctor to update.");
            return;
        }

        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        String name = nameField.getText().trim();
        String specialization = specializationField.getText().trim();
        String contact = contactField.getText().trim();

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE doctors SET name=?, specialization=?, contact=? WHERE doctor_id=?")) {
            ps.setString(1, name);
            ps.setString(2, specialization);
            ps.setString(3, contact);
            ps.setInt(4, doctorId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Doctor updated successfully!");
            loadDoctors();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating doctor: " + ex.getMessage());
        }
    }

    // ✅ Delete doctor
    private void deleteDoctor() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a doctor to delete.");
            return;
        }

        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM doctors WHERE doctor_id=?")) {
            ps.setInt(1, doctorId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Doctor deleted successfully!");
            loadDoctors();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting doctor: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DoctorFrame::new);
    }
}