package project1.doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import project1.util.DBUtil;

public class DoctorAssignNurseDialog extends JDialog {
    private int doctorId;
    private JTable patientTable;
    private JTable nurseTable;
    private DefaultTableModel patientModel;
    private DefaultTableModel nurseModel;

    public DoctorAssignNurseDialog(JFrame parent, int doctorId) {
        super(parent, "Assign Nurse", true);
        this.doctorId = doctorId;

        setSize(900, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setPreferredSize(new Dimension(900, 60));
        JLabel title = new JLabel("🧑‍⚕️ Assign Nurse to Patient", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        // --- Patient table setup ---
        String[] patientColumns = {"Patient ID", "Name", "Diagnosis"};
        patientModel = new DefaultTableModel(patientColumns, 0);
        patientTable = new JTable(patientModel);
        styleTable(patientTable);
        JScrollPane patientScroll = new JScrollPane(patientTable);
        patientScroll.setBorder(BorderFactory.createTitledBorder("Patients"));

        // --- Nurse table setup ---
        String[] nurseColumns = {"Nurse ID", "Name", "Availability"};
        nurseModel = new DefaultTableModel(nurseColumns, 0);
        nurseTable = new JTable(nurseModel);
        styleTable(nurseTable);
        JScrollPane nurseScroll = new JScrollPane(nurseTable);
        nurseScroll.setBorder(BorderFactory.createTitledBorder("Nurses"));

        // Load data
        loadPatients();
        loadNurses();

        // --- Split panel: patients left, nurses right ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, patientScroll, nurseScroll);
        splitPane.setDividerLocation(450);
        splitPane.setResizeWeight(0.5);

        // --- Assign button ---
        JButton assignBtn = new JButton("Assign Selected Nurse");
        assignBtn.setBackground(new Color(46, 204, 113));
        assignBtn.setForeground(Color.WHITE);
        assignBtn.setFont(new Font("Arial", Font.BOLD, 14));
        assignBtn.setFocusPainted(false);
        assignBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        assignBtn.addActionListener(e -> assignSelectedNurse());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(236, 240, 241));
        buttonPanel.add(assignBtn);

        // --- Add panels ---
        add(header, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // --- Style JTable ---
    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(46, 204, 113));
        table.setSelectionForeground(Color.BLACK);
    }

    private void loadPatients() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT patient_id, name, diagnosis FROM Patients")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("patient_id");
                String name = rs.getString("name");
                String diagnosis = rs.getString("diagnosis");
                patientModel.addRow(new Object[]{id, name, diagnosis});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading patients: " + ex.getMessage());
        }
    }

    private void loadNurses() {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT nurse_id, name, availability FROM Nurses")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("nurse_id");
                String name = rs.getString("name");
                boolean available = rs.getBoolean("availability");
                nurseModel.addRow(new Object[]{id, name, available ? "Available" : "Not Available"});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading nurses: " + ex.getMessage());
        }
    }

    private void assignSelectedNurse() {
        int patientRow = patientTable.getSelectedRow();
        int nurseRow = nurseTable.getSelectedRow();

        if (patientRow == -1 || nurseRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select both a patient and a nurse!");
            return;
        }

        int patientId = (int) patientTable.getValueAt(patientRow, 0);
        int nurseId = (int) nurseTable.getValueAt(nurseRow, 0);
        String availability = (String) nurseTable.getValueAt(nurseRow, 2);

        if (!availability.equals("Available")) {
            JOptionPane.showMessageDialog(this, "Selected nurse is not available!");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {
            // ✅ Check patient visit type before assigning
            PreparedStatement psCheck = conn.prepareStatement("SELECT visit_type FROM Patients WHERE patient_id=?");
            psCheck.setInt(1, patientId);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next()) {
                String visitType = rs.getString("visit_type");
                if ("Checkup".equalsIgnoreCase(visitType)) {
                    JOptionPane.showMessageDialog(this, "This patient is only for checkup. No nurse assignment required.");
                    return;
                }
            }

            // ✅ Assign nurse if patient is admitted
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Patient_Nurse_Assignment (patient_id, nurse_id, doctor_id, assignment_date) VALUES (?, ?, ?, NOW())");
            ps.setInt(1, patientId);
            ps.setInt(2, nurseId);
            ps.setInt(3, doctorId);
            ps.executeUpdate();

            // Mark nurse unavailable
            PreparedStatement psUpdate = conn.prepareStatement("UPDATE Nurses SET availability=FALSE WHERE nurse_id=?");
            psUpdate.setInt(1, nurseId);
            psUpdate.executeUpdate();

            JOptionPane.showMessageDialog(this, "Nurse assigned successfully!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error assigning nurse: " + ex.getMessage());
        }
    }
}