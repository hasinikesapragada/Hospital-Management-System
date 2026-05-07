package project1.doctor;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import project1.service.NurseService;
import project1.util.DBUtil;

public class AdmissionsFrame extends JFrame {
    private int doctorId;

    private JTextField patientIdField;
    private JTextField conditionField;

    public AdmissionsFrame(int doctorId) {
        this.doctorId = doctorId;
        setTitle("Patient Admission");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setPreferredSize(new Dimension(500, 60));
        JLabel title = new JLabel("🏥 Admit Patient", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        formPanel.setBackground(new Color(236, 240, 241));

        JLabel patientLabel = new JLabel("Patient ID:");
        patientLabel.setFont(new Font("Arial", Font.BOLD, 14));
        patientIdField = new JTextField(10);

        JLabel conditionLabel = new JLabel("Condition:");
        conditionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        conditionField = new JTextField(15);

        formPanel.add(patientLabel);
        formPanel.add(patientIdField);
        formPanel.add(conditionLabel);
        formPanel.add(conditionField);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton admitBtn = new JButton("Admit Patient");
        admitBtn.setBackground(new Color(46, 204, 113));
        admitBtn.setForeground(Color.WHITE);
        admitBtn.setFont(new Font("Arial", Font.BOLD, 14));
        admitBtn.setFocusPainted(false);
        admitBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        admitBtn.addActionListener(e -> admitPatient());

        buttonPanel.add(admitBtn);

        // --- Add panels to frame ---
        add(header, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void admitPatient() {
        try {
            int patientId = Integer.parseInt(patientIdField.getText().trim());
            String condition = conditionField.getText().trim();

            try (Connection conn = DBUtil.getConnection()) {
                // ✅ Insert admission record
                String sql = "INSERT INTO Admissions (patient_id, doctor_id, condition, admission_date) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, patientId);
                    ps.setInt(2, doctorId);
                    ps.setString(3, condition);
                    ps.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
                    ps.executeUpdate();
                }

                // ✅ Automatically assign nurse
                NurseService service = new NurseService(conn);
                int assignmentId = service.assignNurse(patientId, doctorId);

                if (assignmentId > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Patient admitted successfully!\nNurse assigned (Assignment ID: " + assignmentId + ")");
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Patient admitted successfully!\nBut no nurse available at the moment.");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error admitting patient: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdmissionsFrame(201)); // test with doctorId=201
    }
}