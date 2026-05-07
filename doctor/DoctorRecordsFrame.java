package project1.doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class DoctorRecordsFrame extends JFrame {
    private int doctorId;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField patientIdField, diagnosisField, prescriptionField, dateField;

    public DoctorRecordsFrame(int doctorId) {
        this.doctorId = doctorId;
        setTitle("My Patients' Medical Records");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel patientIdLabel = new JLabel("Patient ID:");
        patientIdLabel.setBounds(30, 30, 100, 30);
        add(patientIdLabel);

        patientIdField = new JTextField();
        patientIdField.setBounds(150, 30, 150, 30);
        add(patientIdField);

        JLabel diagnosisLabel = new JLabel("Diagnosis:");
        diagnosisLabel.setBounds(30, 70, 100, 30);
        add(diagnosisLabel);

        diagnosisField = new JTextField();
        diagnosisField.setBounds(150, 70, 150, 30);
        add(diagnosisField);

        JLabel prescriptionLabel = new JLabel("Prescription:");
        prescriptionLabel.setBounds(30, 110, 100, 30);
        add(prescriptionLabel);

        prescriptionField = new JTextField();
        prescriptionField.setBounds(150, 110, 150, 30);
        add(prescriptionField);

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setBounds(30, 150, 150, 30);
        add(dateLabel);

        dateField = new JTextField();
        dateField.setBounds(180, 150, 150, 30);
        add(dateField);

        JButton addButton = new JButton("Add Record");
        addButton.setBounds(30, 200, 120, 30);
        add(addButton);

        JButton updateButton = new JButton("Update Record");
        updateButton.setBounds(160, 200, 140, 30);
        add(updateButton);

        // ❌ Removed Delete Record button

        tableModel = new DefaultTableModel(
                new String[]{"Record ID", "Patient ID", "Diagnosis", "Prescription", "Date"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(400, 30, 370, 300);
        add(scrollPane);

        loadMyRecords();

        addButton.addActionListener(e -> addRecord());
        updateButton.addActionListener(e -> updateRecord());

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
    }

    // ✅ Load records only for this doctor
    private void loadMyRecords() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT record_id, patient_id, diagnosis, prescription, date FROM medical_records WHERE doctor_id=?")) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("record_id"),
                        rs.getInt("patient_id"),
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

    // ✅ Add record
    private void addRecord() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO medical_records (patient_id, doctor_id, diagnosis, prescription, date) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, Integer.parseInt(patientIdField.getText().trim()));
            ps.setInt(2, doctorId); // always current doctor
            ps.setString(3, diagnosisField.getText().trim());
            ps.setString(4, prescriptionField.getText().trim());
            ps.setDate(5, Date.valueOf(dateField.getText().trim()));
            ps.executeUpdate();
            loadMyRecords();
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
                     "UPDATE medical_records SET patient_id=?, diagnosis=?, prescription=?, date=? WHERE record_id=? AND doctor_id=?")) {
            ps.setInt(1, Integer.parseInt(patientIdField.getText().trim()));
            ps.setString(2, diagnosisField.getText().trim());
            ps.setString(3, prescriptionField.getText().trim());
            ps.setDate(4, Date.valueOf(dateField.getText().trim()));
            ps.setInt(5, recordId);
            ps.setInt(6, doctorId);
            ps.executeUpdate();
            loadMyRecords();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating record: " + ex.getMessage());
        }
    }

    // ✅ Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorRecordsFrame(1)); // test with doctorId=1
    }
}