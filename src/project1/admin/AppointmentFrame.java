package project1.admin;

import project1.ui.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AppointmentFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public AppointmentFrame() {
        setTitle("Appointments");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table setup
        String[] columns = {"Appointment ID", "Patient ID", "Doctor ID", "Slot ID", "Date", "Status"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        UITheme.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton searchBtn = new JButton("Search");
        JButton approveBtn = new JButton("Approve");
        JButton rejectBtn = new JButton("Reject");

        // Apply themed colors
        UITheme.styleButton(addBtn, new Color(46, 204, 113), Color.WHITE);     // Green
        UITheme.styleButton(updateBtn, new Color(52, 152, 219), Color.WHITE);  // Blue
        UITheme.styleButton(deleteBtn, new Color(231, 76, 60), Color.WHITE);   // Red
        UITheme.styleButton(searchBtn, new Color(241, 196, 15), Color.BLACK);  // Yellow
        UITheme.styleButton(approveBtn, new Color(39, 174, 96), Color.WHITE);  // Dark Green
        UITheme.styleButton(rejectBtn, new Color(192, 57, 43), Color.WHITE);   // Dark Red

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        UITheme.stylePanel(buttonPanel);

        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        addBtn.addActionListener(e -> addAppointment());
        updateBtn.addActionListener(e -> updateAppointment());
        deleteBtn.addActionListener(e -> deleteAppointment());
        searchBtn.addActionListener(e -> searchAppointment());
        approveBtn.addActionListener(e -> approveAppointment());
        rejectBtn.addActionListener(e -> rejectAppointment());

        loadAppointments();
        setVisible(true);
    }

    // ✅ Database connection helper
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
    }

    // ✅ Load all appointments
    private void loadAppointments() {
        model.setRowCount(0);
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Appointments")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getInt("slot_id"),
                        rs.getDate("date"),
                        rs.getString("status")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + ex.getMessage());
        }
    }

    // ✅ Add appointment
    private void addAppointment() {
        String patientId = JOptionPane.showInputDialog(this, "Enter Patient ID:");
        String doctorId = JOptionPane.showInputDialog(this, "Enter Doctor ID:");
        String slotId = JOptionPane.showInputDialog(this, "Enter Slot ID:");
        String date = JOptionPane.showInputDialog(this, "Enter Date (yyyy-MM-dd):");
        String status = JOptionPane.showInputDialog(this, "Enter Status (Pending/Approved):");

        if (patientId == null || doctorId == null || slotId == null || date == null || status == null) return;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO Appointments(patient_id, doctor_id, slot_id, date, status) VALUES (?,?,?,?,?)")) {
            ps.setInt(1, Integer.parseInt(patientId));
            ps.setInt(2, Integer.parseInt(doctorId));
            ps.setInt(3, Integer.parseInt(slotId));
            ps.setDate(4, Date.valueOf(date));
            ps.setString(5, status);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Appointment added successfully!");
            loadAppointments();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding appointment: " + ex.getMessage());
        }
    }

    // ✅ Update appointment
    private void updateAppointment() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment to update!");
            return;
        }
        int appointmentId = (int) model.getValueAt(row, 0);
        String newStatus = JOptionPane.showInputDialog(this, "Enter new status (Pending/Approved):");
        if (newStatus == null || newStatus.trim().isEmpty()) return;

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("UPDATE Appointments SET status=? WHERE appointment_id=?")) {
            ps.setString(1, newStatus);
            ps.setInt(2, appointmentId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Appointment updated successfully!");
            loadAppointments();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating appointment: " + ex.getMessage());
        }
    }

    // ✅ Delete appointment
    private void deleteAppointment() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment to delete!");
            return;
        }
        int appointmentId = (int) model.getValueAt(row, 0);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Appointments WHERE appointment_id=?")) {
            ps.setInt(1, appointmentId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Appointment deleted successfully!");
            loadAppointments();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error deleting appointment: " + ex.getMessage());
        }
    }

    // ✅ Search appointment
    private void searchAppointment() {
        String patientId = JOptionPane.showInputDialog(this, "Enter Patient ID to search:");
        if (patientId == null || patientId.trim().isEmpty()) return;

        model.setRowCount(0);
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Appointments WHERE patient_id=?")) {
            ps.setInt(1, Integer.parseInt(patientId));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getInt("slot_id"),
                        rs.getDate("date"),
                        rs.getString("status")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error searching appointment: " + ex.getMessage());
        }
    }

    // ✅ Approve appointment
    private void approveAppointment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment to approve.");
            return;
        }
        int appointmentId = (int) model.getValueAt(selectedRow, 0);
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("UPDATE Appointments SET status='BOOKED' WHERE appointment_id=?")) {
            ps.setInt(1, appointmentId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Appointment approved!");
            loadAppointments();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error approving appointment: " + ex.getMessage());
        }
    }

    // ✅ Reject appointment
    private void rejectAppointment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment to reject.");
            return;
        }
        int appointmentId = (int) model.getValueAt(selectedRow, 0);
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("UPDATE Appointments SET status='REJECTED' WHERE appointment_id=?")) {
            ps.setInt(1, appointmentId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Appointment rejected!");
            loadAppointments();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error rejecting appointment: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AppointmentFrame::new);
    }
}