package project1.doctor;

import javax.swing.JOptionPane;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SlotService {

    private static final String URL = "jdbc:mysql://localhost:3306/hospital";
    private static final String USER = "root";
    private static final String PASSWORD = "1234"; // replace with actual password

    // Create a new slot (doctor availability)
    public int create(Slot slot) {
        String sql = "INSERT INTO doctor_slots (doctor_id, start_time, end_time, week_start, week_end, active) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, slot.getDoctorId());
            ps.setTimestamp(2, Timestamp.valueOf(slot.getStartTime()));
            ps.setTimestamp(3, Timestamp.valueOf(slot.getEndTime()));
            ps.setDate(4, Date.valueOf(slot.getWeekStart()));
            ps.setDate(5, Date.valueOf(slot.getWeekEnd()));
            ps.setBoolean(6, slot.isActive());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                JOptionPane.showMessageDialog(null, "Insert failed: no rows affected!");
                return -1;
            }

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                JOptionPane.showMessageDialog(null, "Slot inserted successfully with ID: " + generatedId);
                return generatedId;
            } else {
                JOptionPane.showMessageDialog(null, "Insert succeeded but no ID returned!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error (create): " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // failed
    }

    // Update an existing slot (doctor availability)
    public boolean update(Slot slot) {
        String sql = "UPDATE doctor_slots SET doctor_id=?, start_time=?, end_time=?, week_start=?, week_end=?, active=? " +
                     "WHERE slot_id=?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, slot.getDoctorId());
            ps.setTimestamp(2, Timestamp.valueOf(slot.getStartTime()));
            ps.setTimestamp(3, Timestamp.valueOf(slot.getEndTime()));
            ps.setDate(4, Date.valueOf(slot.getWeekStart()));
            ps.setDate(5, Date.valueOf(slot.getWeekEnd()));
            ps.setBoolean(6, slot.isActive());
            ps.setInt(7, slot.getSlotId());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Slot updated successfully!");
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Slot update failed: no rows affected!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error (update): " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Find availability slots for a doctor
    public List<Slot> findAvailabilityForDoctor(int doctorId) {
        List<Slot> slots = new ArrayList<>();
        String sql = "SELECT * FROM doctor_slots WHERE doctor_id=? AND end_time > NOW() AND active=1";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Slot slot = new Slot();
                slot.setSlotId(rs.getInt("slot_id"));
                slot.setDoctorId(rs.getInt("doctor_id"));
                slot.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                slot.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                slot.setWeekStart(rs.getDate("week_start").toLocalDate());
                slot.setWeekEnd(rs.getDate("week_end").toLocalDate());
                slot.setActive(rs.getBoolean("active"));
                slots.add(slot);
            }

            if (slots.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No upcoming availability found for Doctor ID: " + doctorId);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Error (findAvailability): " + e.getMessage());
            e.printStackTrace();
        }
        return slots;
    }
}