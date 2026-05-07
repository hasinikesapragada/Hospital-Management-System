package project1.dao;

import java.sql.*;
import java.util.*;
import project1.nurse.PatientNurseAssignment;

public class AssignmentDAO {
    private final Connection conn;

    public AssignmentDAO(Connection conn) {
        this.conn = conn;
    }

    // Get all assignments for a given nurse
    public List<PatientNurseAssignment> getAssignmentsByNurse(int nurseId) throws SQLException {
        List<PatientNurseAssignment> list = new ArrayList<>();
        String sql = "SELECT assignment_id, patient_id, doctor_id, assignment_date " +
                     "FROM Patient_Nurse_Assignment WHERE nurse_id=? AND status='Active'";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            ps.setInt(1, nurseId);

            while (rs.next()) {
                PatientNurseAssignment a = new PatientNurseAssignment(
                    rs.getInt("assignment_id"),
                    rs.getInt("patient_id"),
                    rs.getInt("doctor_id"),
                    rs.getDate("assignment_date")
                );
                list.add(a);
            }
        }
        return list;
    }
}