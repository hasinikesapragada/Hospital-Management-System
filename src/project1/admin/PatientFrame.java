package project1.admin;

import project1.ui.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PatientFrame extends JFrame {
    private JTextField idField, nameField, ageField;
    private JTable table;
    private DefaultTableModel tableModel;

    public PatientFrame() {
        setTitle("Patient Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // --- Labels & Fields ---
        JLabel idLabel = new JLabel("Patient ID:");
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

        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setBounds(30, 110, 100, 30);
        UITheme.styleLabel(ageLabel);
        add(ageLabel);

        ageField = new JTextField();
        ageField.setBounds(150, 110, 150, 30);
        add(ageField);

        // --- Buttons ---
        JButton addButton = new JButton("Add");
        addButton.setBounds(30, 160, 100, 30);
        UITheme.styleButton(addButton, new Color(46, 204, 113), Color.WHITE); // Green
        add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(140, 160, 100, 30);
        UITheme.styleButton(updateButton, new Color(52, 152, 219), Color.WHITE); // Blue
        add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(250, 160, 100, 30);
        UITheme.styleButton(deleteButton, new Color(231, 76, 60), Color.WHITE); // Red
        add(deleteButton);

        JButton searchButton = new JButton("Search");
        searchButton.setBounds(360, 160, 100, 30);
        UITheme.styleButton(searchButton, new Color(241, 196, 15), Color.BLACK); // Yellow
        add(searchButton);

        // --- Table setup ---
        tableModel = new DefaultTableModel(new String[]{"Patient ID", "Name", "Age"}, 0);
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 210, 520, 120);
        add(scrollPane);

        // --- Load patients from DB at startup ---
        loadPatients();

        // --- Button actions ---
        addButton.addActionListener(e -> addPatient());
        updateButton.addActionListener(e -> updatePatient());
        deleteButton.addActionListener(e -> deletePatient());
        searchButton.addActionListener(e -> searchPatient());

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hospital"; // replace with your DB name
        String user = "root"; // replace with your MySQL username
        String password = "1234"; // replace with your MySQL password
        return DriverManager.getConnection(url, user, password);
    }

    // ✅ Load patients into JTable
    private void loadPatients() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM patients")) {

            tableModel.setRowCount(0); // clear table
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("patient_id"),
                        rs.getString("name"),
                        rs.getInt("age")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage());
        }
    }

    // ✅ Add patient
    private void addPatient() {
        String name = nameField.getText().trim();
        String age = ageField.getText().trim();

        if (name.isEmpty() || age.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Age must be filled!");
            return;
        }

        try {
            int ageVal = Integer.parseInt(age);
            try (Connection conn = connect();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO patients (name, age, gender, contact) VALUES (?, ?, ?, ?)")) {
                ps.setString(1, name);
                ps.setInt(2, ageVal);
                ps.setString(3, ""); // placeholder for gender
                ps.setString(4, ""); // placeholder for contact
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Patient added successfully!");
                loadPatients();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a number!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding patient: " + ex.getMessage());
        }
    }

    // ✅ Update patient
    private void updatePatient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a patient to update.");
            return;
        }

        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        String name = nameField.getText().trim();
        String age = ageField.getText().trim();

        try {
            int ageVal = Integer.parseInt(age);
            try (Connection conn = connect();
                 PreparedStatement ps = conn.prepareStatement(
                         "UPDATE patients SET name=?, age=? WHERE patient_id=?")) {
                ps.setString(1, name);
                ps.setInt(2, ageVal);
                ps.setInt(3, patientId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Patient updated successfully!");
                loadPatients();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Age must be a number!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating patient: " + ex.getMessage());
        }
    }

    // ✅ Delete patient
    private void deletePatient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a patient to delete.");
            return;
        }

        int patientId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM patients WHERE patient_id=?")) {
            ps.setInt(1, patientId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Patient deleted successfully!");
            loadPatients();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting patient: " + ex.getMessage());
        }
    }

    // ✅ Search patient by ID or Name
    private void searchPatient() {
        String searchId = idField.getText().trim();
        String searchName = nameField.getText().trim();

        if (searchId.isEmpty() && searchName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Patient ID or Name to search!");
            return;
        }

        boolean found = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String id = tableModel.getValueAt(i, 0).toString();
            String name = tableModel.getValueAt(i, 1).toString();
            if (id.equalsIgnoreCase(searchId) || name.equalsIgnoreCase(searchName)) {
                table.setRowSelectionInterval(i, i);
                found = true;
                break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "No matching patient found.");
        }
    }

    // ✅ Main method to run independently
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PatientFrame::new);
    }
}