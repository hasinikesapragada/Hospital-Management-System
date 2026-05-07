package project1.doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SlotService {
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
    }

    // ✅ Create new slot
    public int create(Slot slot) throws SQLException {
        String sql = "INSERT INTO slots (doctor_id, start_time, end_time, week_start, week_end, is_auto_approvable, is_active) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, slot.getDoctorId());
            ps.setTimestamp(2, Timestamp.valueOf(slot.getStartTime()));
            ps.setTimestamp(3, Timestamp.valueOf(slot.getEndTime()));
            ps.setDate(4, Date.valueOf(slot.getWeekStart()));
            ps.setDate(5, Date.valueOf(slot.getWeekEnd()));
            ps.setBoolean(6, slot.isAutoApprovable());
            ps.setBoolean(7, slot.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    // ✅ Find slots by doctor
    public List<Slot> findByDoctor(int doctorId) throws SQLException {
        String sql = "SELECT * FROM slots WHERE doctor_id=? AND is_active=TRUE";
        List<Slot> slots = new ArrayList<>();
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Slot slot = new Slot();
                    slot.setSlotId(rs.getInt("slot_id"));
                    slot.setDoctorId(rs.getInt("doctor_id"));
                    slot.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                    slot.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                    slot.setWeekStart(rs.getDate("week_start").toLocalDate());
                    slot.setWeekEnd(rs.getDate("week_end").toLocalDate());
                    slot.setAutoApprovable(rs.getBoolean("is_auto_approvable"));
                    slot.setActive(rs.getBoolean("is_active"));
                    slots.add(slot);
                }
            }
        }
        return slots;
    }

    // ✅ Check if slot has appointments
    public boolean hasAppointments(int slotId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM appointments WHERE slot_id=?";
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // true if appointments exist
                }
            }
        }
        return false;
    }

    // ✅ Update slot with restriction
    public boolean update(Slot slot) throws SQLException {
        // Restrict updates if slot has appointments
        if (hasAppointments(slot.getSlotId())) {
            System.out.println("Update blocked: Slot has booked appointments.");
            return false;
        }

        String sql = "UPDATE slots SET doctor_id=?, start_time=?, end_time=?, week_start=?, week_end=?, is_auto_approvable=?, is_active=? WHERE slot_id=?";
        try (Connection con = connect();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, slot.getDoctorId());
            ps.setTimestamp(2, Timestamp.valueOf(slot.getStartTime()));
            ps.setTimestamp(3, Timestamp.valueOf(slot.getEndTime()));
            ps.setDate(4, Date.valueOf(slot.getWeekStart()));
            ps.setDate(5, Date.valueOf(slot.getWeekEnd()));
            ps.setBoolean(6, slot.isAutoApprovable());
            ps.setBoolean(7, slot.isActive());
            ps.setInt(8, slot.getSlotId());
            return ps.executeUpdate() > 0;
        }
    }
}