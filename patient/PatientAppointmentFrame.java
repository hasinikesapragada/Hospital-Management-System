package project1.patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class PatientAppointmentFrame extends JFrame {
    private int patientId;
    private JTable table, slotTable;
    private DefaultTableModel tableModel, slotTableModel;
    private JComboBox<String> doctorDropdown;
    private JTextField dateField, timeField;

    public PatientAppointmentFrame(int patientId) {
        this.patientId = patientId;
        setTitle("My Appointments");
        setSize(720, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel doctorLabel = new JLabel("Select Doctor:");
        doctorLabel.setBounds(30, 30, 120, 30);
        add(doctorLabel);

        doctorDropdown = new JComboBox<>();
        doctorDropdown.setBounds(150, 30, 250, 30);
        add(doctorDropdown);
        loadDoctors(); // populate dropdown

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setBounds(30, 70, 150, 30);
        add(dateLabel);

        dateField = new JTextField();
        dateField.setBounds(180, 70, 150, 30);
        add(dateField);

        JLabel timeLabel = new JLabel("Time (HH:MM):");
        timeLabel.setBounds(30, 110, 100, 30);
        add(timeLabel);

        timeField = new JTextField();
        timeField.setBounds(150, 110, 150, 30);
        add(timeField);

        JButton addButton = new JButton("Book Appointment");
        addButton.setBounds(30, 160, 150, 30);
        add(addButton);

        JButton deleteButton = new JButton("Cancel Appointment");
        deleteButton.setBounds(200, 160, 150, 30);
        add(deleteButton);

        JButton loadSlotsButton = new JButton("Load Doctor Slots");
        loadSlotsButton.setBounds(30, 200, 200, 30);
        add(loadSlotsButton);

        // Table for appointments
        tableModel = new DefaultTableModel(new String[]{"Appointment ID", "Doctor ID", "Date", "Time", "Status"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(350, 30, 350, 200);
        add(scrollPane);

        // Table for slots
        slotTableModel = new DefaultTableModel(new String[]{"Slot ID", "Doctor ID", "Start", "End", "Auto-Approve"}, 0);
        slotTable = new JTable(slotTableModel);
        JScrollPane slotScroll = new JScrollPane(slotTable);
        slotScroll.setBounds(30, 250, 670, 180);
        add(slotScroll);

        // Load appointments for this patient
        loadMyAppointments();

        // Button actions
        addButton.addActionListener(e -> addAppointment());
        deleteButton.addActionListener(e -> deleteAppointment());
        loadSlotsButton.addActionListener(e -> {
            try {
                int doctorId = getSelectedDoctorId();
                loadDoctorSlots(doctorId);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Select a valid doctor first.");
            }
        });

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
    }

    // ✅ Load doctors into dropdown
    private void loadDoctors() {
        try (Connection conn = connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT doctor_id, name, specialization FROM doctors")) {
            while (rs.next()) {
                int id = rs.getInt("doctor_id");
                String name = rs.getString("name");
                String spec = rs.getString("specialization");
                doctorDropdown.addItem(id + " - " + name + " (" + spec + ")");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading doctors: " + e.getMessage());
        }
    }

    // ✅ Extract doctor ID from dropdown
    private int getSelectedDoctorId() {
        String selected = (String) doctorDropdown.getSelectedItem();
        return Integer.parseInt(selected.split(" - ")[0]);
    }

    // ✅ Load appointments only for this patient
    private void loadMyAppointments() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments WHERE patient_id=?")) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("appointment_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("date"),
                        rs.getString("time"),
                        rs.getString("status")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage());
        }
    }

    // ✅ Load slots for doctor
    private void loadDoctorSlots(int doctorId) {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM slots WHERE doctor_id=? AND is_active=TRUE")) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            slotTableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("slot_id"),
                        rs.getInt("doctor_id"),
                        rs.getTimestamp("start_time"),
                        rs.getTimestamp("end_time"),
                        rs.getBoolean("is_auto_approvable")
                };
                slotTableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading slots: " + e.getMessage());
        }
    }

    // ✅ Add appointment for this patient (linked to slot)
    private void addAppointment() {
        try (Connection conn = connect()) {
            int doctorId = getSelectedDoctorId();
            Date date = Date.valueOf(dateField.getText().trim());
            String time = timeField.getText().trim();

            // Check if requested slot exists
            PreparedStatement slotCheck = conn.prepareStatement(
                "SELECT * FROM slots WHERE doctor_id=? AND ? BETWEEN start_time AND end_time AND is_active=TRUE");
            slotCheck.setInt(1, doctorId);
            slotCheck.setTimestamp(2, Timestamp.valueOf(date.toString() + " " + time + ":00"));
            ResultSet rs = slotCheck.executeQuery();

            if (rs.next()) {
                boolean autoApprove = rs.getBoolean("is_auto_approvable");
                String status = autoApprove ? "BOOKED" : "PENDING";

                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO appointments (patient_id, doctor_id, date, time, status, slot_id) VALUES (?, ?, ?, ?, ?, ?)");
                ps.setInt(1, patientId);
                ps.setInt(2, doctorId);
                ps.setDate(3, date);
                ps.setString(4, time);
                ps.setString(5, status);
                ps.setInt(6, rs.getInt("slot_id"));
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Appointment " + status);
                loadMyAppointments();
            } else {
                JOptionPane.showMessageDialog(this, "No valid slot available for this time.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error booking appointment: " + ex.getMessage());
        }
    }

    // ✅ Delete appointment
    private void deleteAppointment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment to cancel.");
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM appointments WHERE appointment_id=? AND patient_id=?")) {
            ps.setInt(1, appointmentId);
            ps.setInt(2, patientId);
            ps.executeUpdate();
            loadMyAppointments();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error canceling appointment: " + ex.getMessage());
        }
    }

    // ✅ Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientAppointmentFrame(101)); // test with patientId=101
    }
}