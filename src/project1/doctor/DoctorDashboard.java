package project1.doctor;

import javax.swing.*;
import project1.login.Login;
import project1.util.DBUtil;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DoctorDashboard extends JFrame {
    private int doctorId;

    // Define two main colors
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80);   // dark gray/navy
    private static final Color SECONDARY_COLOR = new Color(245, 245, 245); // light gray/white

    public DoctorDashboard(int doctorId) {
        this.doctorId = doctorId;
        setTitle("Doctor Portal");
        setSize(900, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(900, 60));
        JLabel title = new JLabel("👨‍⚕️ Doctor Dashboard", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.setBackground(SECONDARY_COLOR);

        JButton appointmentsBtn = createStyledButton("📅 My Appointments");
        appointmentsBtn.addActionListener(e -> new DoctorAppointmentsFrame(doctorId));

        JButton recordsBtn = createStyledButton("📑 My Patients' Records");
        recordsBtn.addActionListener(e -> new DoctorRecordsFrame(doctorId));

        JButton profileBtn = createStyledButton("👤 My Profile");
        profileBtn.addActionListener(e -> new DoctorProfileFrame(doctorId));

        JButton slotsBtn = createStyledButton("⏰ My Slots");
        slotsBtn.addActionListener(e -> new DoctorSlotsFrame());

        JButton assignNurseBtn = createStyledButton("🧑‍⚕️ Assign Nurse");
        assignNurseBtn.addActionListener(e -> new DoctorAssignNurseDialog(this, doctorId));

        JButton logoutBtn = createStyledButton("🚪 Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new Login();
        });

        buttonPanel.add(appointmentsBtn);
        buttonPanel.add(recordsBtn);
        buttonPanel.add(profileBtn);
        buttonPanel.add(slotsBtn);
        buttonPanel.add(assignNurseBtn);
        buttonPanel.add(logoutBtn);

        add(header, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // --- Styled Button Helper ---
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return button;
    }

    // --- Discharge logic integrated with visit type ---
    private void dischargePatient(int patientId) {
        try (Connection conn = DBUtil.getConnection()) {
            PreparedStatement psCheck = conn.prepareStatement("SELECT visit_type FROM Patients WHERE patient_id=?");
            psCheck.setInt(1, patientId);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                String visitType = rs.getString("visit_type");

                if ("Checkup".equalsIgnoreCase(visitType)) {
                    PreparedStatement psUpdate = conn.prepareStatement(
                            "UPDATE Patients SET status='Discharged' WHERE patient_id=?");
                    psUpdate.setInt(1, patientId);
                    psUpdate.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Patient discharged (Checkup only). No nurse release needed.");
                    return;
                }

                // Admitted patient → release nurse
                PreparedStatement ps1 = conn.prepareStatement(
                        "UPDATE Patients SET status='Discharged' WHERE patient_id=?");
                ps1.setInt(1, patientId);
                ps1.executeUpdate();

                PreparedStatement ps2 = conn.prepareStatement(
                        "UPDATE Patient_Nurse_Assignment SET status='Released' WHERE patient_id=? AND status='Active'");
                ps2.setInt(1, patientId);
                ps2.executeUpdate();

                PreparedStatement ps3 = conn.prepareStatement(
                        "UPDATE Nurses SET availability=TRUE WHERE nurse_id IN (SELECT nurse_id FROM Patient_Nurse_Assignment WHERE patient_id=?)");
                ps3.setInt(1, patientId);
                ps3.executeUpdate();

                JOptionPane.showMessageDialog(this, "Patient discharged and nurse released successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Patient not found!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error discharging patient: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorDashboard(201)); // test with doctorId=201
    }
}