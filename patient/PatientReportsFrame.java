package project1.patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class PatientReportsFrame extends JFrame {
    private int patientId;
    private JTable table;
    private DefaultTableModel tableModel;

    public PatientReportsFrame(int patientId) {
        this.patientId = patientId;
        setTitle("My Medical Records");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(
                new String[]{"Record ID", "Doctor ID", "Diagnosis", "Prescription", "Date"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        loadMyRecords();

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
    }

    // ✅ Load records only for this patient
    private void loadMyRecords() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT record_id, doctor_id, diagnosis, prescription, date FROM medical_records WHERE patient_id=?")) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("record_id"),
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

    // ✅ Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientReportsFrame(101)); // test with patientId=101
    }
}