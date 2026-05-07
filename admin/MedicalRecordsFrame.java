package project1.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class MedicalRecordsFrame extends JFrame {
    private JTextField patientIdField, doctorIdField, diagnosisField, prescriptionField, dateField, searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public MedicalRecordsFrame() {
        setTitle("Medical Records Management");
        setSize(950, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // --- Search Section ---
        JLabel searchLabel = new JLabel("Search by Patient ID:");
        searchLabel.setBounds(30, 20, 150, 30);
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(180, 20, 150, 30);
        add(searchField);

        JButton searchButton = new JButton("Search");
        searchButton.setBounds(350, 20, 100, 30);
        add(searchButton);

        // --- Input Fields ---
        JLabel patientIdLabel = new JLabel("Patient ID:");
        patientIdLabel.setBounds(30, 70, 100, 30);
        add(patientIdLabel);

        patientIdField = new JTextField();
        patientIdField.setBounds(150, 70, 150, 30);
        add(patientIdField);

        JLabel doctorIdLabel = new JLabel("Doctor ID:");
        doctorIdLabel.setBounds(30, 110, 100, 30);
        add(doctorIdLabel);

        doctorIdField = new JTextField();
        doctorIdField.setBounds(150, 110, 150, 30);
        add(doctorIdField);

        JLabel diagnosisLabel = new JLabel("Diagnosis:");
        diagnosisLabel.setBounds(30, 150, 100, 30);
        add(diagnosisLabel);

        diagnosisField = new JTextField();
        diagnosisField.setBounds(150, 150, 150, 30);
        add(diagnosisField);

        JLabel prescriptionLabel = new JLabel("Prescription:");
        prescriptionLabel.setBounds(30, 190, 100, 30);
        add(prescriptionLabel);

        prescriptionField = new JTextField();
        prescriptionField.setBounds(150, 190, 150, 30);
        add(prescriptionField);

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setBounds(30, 230, 150, 30);
        add(dateLabel);

        dateField = new JTextField();
        dateField.setBounds(180, 230, 150, 30);
        add(dateField);

        // --- Buttons ---
        JButton addButton = new JButton("Add");
        addButton.setBounds(30, 270, 100, 30);
        add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(140, 270, 100, 30);
        add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(250, 270, 100, 30);
        add(deleteButton);

        // --- Table ---
        tableModel = new DefaultTableModel(new String[]{"Record ID", "Patient ID", "Doctor ID", "Diagnosis", "Prescription", "Date"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(400, 70, 520, 300);
        add(scrollPane);

        // --- Load Records Initially ---
        loadRecords();

        // --- Button Actions ---
        addButton.addActionListener(e -> addRecord());
        updateButton.addActionListener(e -> updateRecord());
        deleteButton.addActionListener(e -> deleteRecord());
        searchButton.addActionListener(e -> searchRecords());

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hospital";
        String user = "root";
        String password = "1234";
        return DriverManager.getConnection(url, user, password);
    }

    // ✅ Load all records
    private void loadRecords() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM medical_records")) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("record_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getString("diagnosis"),
                        rs.getString("prescription"),
                        rs.getDate("date")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading records: " + e.getMessage());
        }
    }

    // ✅ Search records by patient_id
    private void searchRecords() {
        String patientId = searchField.getText().trim();
        if (patientId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Patient ID!");
            return;
        }

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM medical_records WHERE patient_id=?")) {
            ps.setInt(1, Integer.parseInt(patientId));
            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("record_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getString("diagnosis"),
                        rs.getString("prescription"),
                        rs.getDate("date")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error searching records: " + ex.getMessage());
        }
    }

    // ✅ Add record
    private void addRecord() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO medical_records (patient_id, doctor_id, diagnosis, prescription, date) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, Integer.parseInt(patientIdField.getText().trim()));
            ps.setInt(2, Integer.parseInt(doctorIdField.getText().trim()));
            ps.setString(3, diagnosisField.getText().trim());
            ps.setString(4, prescriptionField.getText().trim());
            ps.setDate(5, Date.valueOf(dateField.getText().trim()));
            ps.executeUpdate();
            loadRecords();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding record: " + ex.getMessage());
        }
    }

    // ✅ Update record
    private void updateRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to update.");
            return;
        }

        int recordId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE medical_records SET patient_id=?, doctor_id=?, diagnosis=?, prescription=?, date=? WHERE record_id=?")) {
            ps.setInt(1, Integer.parseInt(patientIdField.getText().trim()));
            ps.setInt(2, Integer.parseInt(doctorIdField.getText().trim()));
            ps.setString(3, diagnosisField.getText().trim());
            ps.setString(4, prescriptionField.getText().trim());
            ps.setDate(5, Date.valueOf(dateField.getText().trim()));
            ps.setInt(6, recordId);
            ps.executeUpdate();
            loadRecords();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating record: " + ex.getMessage());
        }
    }

    // ✅ Delete record
    private void deleteRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to delete.");
            return;
        }

        int recordId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM medical_records WHERE record_id=?")) {
            ps.setInt(1, recordId);
            ps.executeUpdate();
            loadRecords();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting record: " + ex.getMessage());
        }
    }

    // ✅ Main method to run independently
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MedicalRecordsFrame());
    }
}