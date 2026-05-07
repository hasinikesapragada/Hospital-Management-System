package project1.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class AppointmentFrame extends JFrame {
    private JTextField patientIdField, doctorIdField, dateField, timeField, searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public AppointmentFrame() {
        setTitle("Appointment Management");
        setSize(900, 550);
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

        JButton pendingButton = new JButton("View Pending");
        pendingButton.setBounds(460, 20, 150, 30);
        add(pendingButton);

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

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setBounds(30, 150, 150, 30);
        add(dateLabel);

        dateField = new JTextField();
        dateField.setBounds(180, 150, 150, 30);
        add(dateField);

        JLabel timeLabel = new JLabel("Time (HH:MM):");
        timeLabel.setBounds(30, 190, 100, 30);
        add(timeLabel);

        timeField = new JTextField();
        timeField.setBounds(150, 190, 150, 30);
        add(timeField);

        // --- Buttons ---
        JButton addButton = new JButton("Add");
        addButton.setBounds(30, 240, 100, 30);
        add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(140, 240, 100, 30);
        add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(250, 240, 100, 30);
        add(deleteButton);

        JButton approveButton = new JButton("Approve");
        approveButton.setBounds(360, 240, 100, 30);
        add(approveButton);

        JButton rejectButton = new JButton("Reject");
        rejectButton.setBounds(470, 240, 100, 30);
        add(rejectButton);

        // --- Table ---
        tableModel = new DefaultTableModel(new String[]{"Appointment ID", "Patient ID", "Doctor ID", "Date", "Time", "Status"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(400, 70, 470, 350);
        add(scrollPane);

        // --- Load appointments initially ---
        loadAppointments();

        // --- Button Actions ---
        addButton.addActionListener(e -> addAppointment());
        updateButton.addActionListener(e -> updateAppointment());
        deleteButton.addActionListener(e -> deleteAppointment());
        searchButton.addActionListener(e -> searchAppointments());
        pendingButton.addActionListener(e -> loadPendingAppointments());
        approveButton.addActionListener(e -> approveAppointment());
        rejectButton.addActionListener(e -> rejectAppointment());

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hospital";
        String user = "root";
        String password = "1234";
        return DriverManager.getConnection(url, user, password);
    }

    // ✅ Load all appointments
    private void loadAppointments() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM appointments")) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
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

    // ✅ Load only pending appointments
    private void loadPendingAppointments() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments WHERE status='PENDING'")) {
            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("date"),
                        rs.getString("time"),
                        rs.getString("status")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading pending appointments: " + e.getMessage());
        }
    }

    // ✅ Search appointments by patient_id
    private void searchAppointments() {
        String patientId = searchField.getText().trim();
        if (patientId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Patient ID!");
            return;
        }

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments WHERE patient_id=?")) {
            ps.setInt(1, Integer.parseInt(patientId));
            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("date"),
                        rs.getString("time"),
                        rs.getString("status")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error searching appointments: " + ex.getMessage());
        }
    }

    // ✅ Add appointment
    private void addAppointment() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO appointments (patient_id, doctor_id, date, time, status) VALUES (?, ?, ?, ?, 'BOOKED')")) {
            ps.setInt(1, Integer.parseInt(patientIdField.getText().trim()));
            ps.setInt(2, Integer.parseInt(doctorIdField.getText().trim()));
            ps.setDate(3, Date.valueOf(dateField.getText().trim()));
            ps.setString(4, timeField.getText().trim());
            ps.executeUpdate();
            loadAppointments();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding appointment: " + ex.getMessage());
        }
    }

    // ✅ Update appointment
    private void updateAppointment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment to update.");
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE appointments SET patient_id=?, doctor_id=?, date=?, time=? WHERE appointment_id=?")) {
            ps.setInt(1, Integer.parseInt(patientIdField.getText().trim()));
            ps.setInt(2, Integer.parseInt(doctorIdField.getText().trim()));
            ps.setDate(3, Date.valueOf(dateField.getText().trim()));
            ps.setString(4, timeField.getText().trim());
            ps.setInt(5, appointmentId);
            ps.executeUpdate();
            loadAppointments();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating appointment: " + ex.getMessage());
        }
    }

    // ✅ Delete appointment
    private void deleteAppointment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment to delete.");
            return;
        }

        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM appointments WHERE appointment_id=?")) {
            ps.setInt(1, appointmentId);
            ps.executeUpdate();
            loadAppointments();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting appointment: " + ex.getMessage());
        }
    }
    
 // Approve appointment
    private void approveAppointment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment to approve.");
            return;
        }
        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("UPDATE appointments SET status='BOOKED' WHERE appointment_id=?")) {
            ps.setInt(1, appointmentId);
            ps.executeUpdate();
            loadAppointments();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error approving appointment: " + ex.getMessage());
        }
    }

    // Reject appointment
    private void rejectAppointment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment to reject.");
            return;
        }
        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("UPDATE appointments SET status='REJECTED' WHERE appointment_id=?")) {
            ps.setInt(1, appointmentId);
            ps.executeUpdate();
            loadAppointments();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error rejecting appointment: " + ex.getMessage());
        }
    }

    // ✅

    // ✅ Main method to run independently
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppointmentFrame());
    }
}