package project1.doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DoctorAppointmentsFrame extends JFrame {
    private int doctorId;
    private JTable table;
    private DefaultTableModel tableModel;

    public DoctorAppointmentsFrame(int doctorId) {
        this.doctorId = doctorId;
        setTitle("Doctor Portal - My Appointments");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setPreferredSize(new Dimension(800, 60));
        JLabel title = new JLabel("📅 My Appointments", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        // --- Table setup ---
        tableModel = new DefaultTableModel(
                new String[]{"Appointment ID", "Patient ID", "Date", "Time"}, 0);
        table = new JTable(tableModel);

        // Style table
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(46, 204, 113));
        table.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Add panels ---
        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load appointments
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