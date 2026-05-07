package project1.doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class DoctorAppointmentsFrame extends JFrame {
    private int doctorId;
    private JTable table;
    private DefaultTableModel tableModel;

    public DoctorAppointmentsFrame(int doctorId) {
        this.doctorId = doctorId;
        setTitle("My Appointments");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(
                new String[]{"Appointment ID", "Patient ID", "Date", "Time"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        loadMyAppointments();

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
    }

    // ✅ Load appointments only for this doctor
    private void loadMyAppointments() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT appointment_id, patient_id, date, time FROM appointments WHERE doctor_id=?")) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getDate("date"),
                        rs.getString("time")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage());
        }
    }

    // ✅ Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorAppointmentsFrame(201)); // test with doctorId=201
    }
}