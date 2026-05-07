package project1.doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class DoctorRecordsFrame extends JFrame {
    private int doctorId;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField patientIdField, diagnosisField, prescriptionField, dateField;

    public DoctorRecordsFrame(int doctorId) {
        this.doctorId = doctorId;
        setTitle("Doctor Portal - My Patients' Medical Records");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setPreferredSize(new Dimension(900, 60));
        JLabel title = new JLabel("📑 My Patients' Medical Records", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        formPanel.setBackground(new Color(236, 240, 241));

        JLabel patientIdLabel = new JLabel("Patient ID:");
        patientIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        patientIdField = new JTextField();

        JLabel diagnosisLabel = new JLabel("Diagnosis:");
        diagnosisLabel.setFont(new Font("Arial", Font.BOLD, 14));
        diagnosisField = new JTextField();

        JLabel prescriptionLabel = new JLabel("Prescription:");
        prescriptionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        prescriptionField = new JTextField();

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateField = new JTextField();

        formPanel.add(patientIdLabel);
        formPanel.add(patientIdField);
        formPanel.add(diagnosisLabel);
        formPanel.add(diagnosisField);
        formPanel.add(prescriptionLabel);
        formPanel.add(prescriptionField);
        formPanel.add(dateLabel);
        formPanel.add(dateField);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton addButton = new JButton("Add Record");
        styleButton(addButton, new Color(46, 204, 113), Color.WHITE);
        addButton.addActionListener(e -> addRecord());

        JButton updateButton = new JButton("Update Record");
        styleButton(updateButton, new Color(52, 152, 219), Color.WHITE);
        updateButton.addActionListener(e -> updateRecord());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);

        // --- Table setup ---
        tableModel = new DefaultTableModel(
                new String[]{"Record ID", "Patient ID", "Diagnosis", "Prescription", "Date"}, 0);
        table = new JTable(tableModel);

        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Medical Records"));

        // --- Add panels ---
        add(header, BorderLayout.NORTH);
        add(formPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load records
        loadMyRecords();

        setVisible(true);
    }

    // --- Style Button Helper ---
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    // --- Style Table Helper ---
    private void styleTable(JTable table) {
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(46, 204, 113));
        table.setSelectionForeground(Color.BLACK);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
    }

    // ✅ Load records only for this doctor
    private void loadMyRecords() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT record_id, patient_id, diagnosis, prescription, date FROM medical_records WHERE doctor_id=?")) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("record_id"),
                        rs.getInt("patient_id"),
                        rs.getString("diagnosis"),
                        rs.getString("prescription"),
                        rs.getDate("date")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading records: " + e.getMessage());
        }
    }

    // ✅ Add record
    private void addRecord() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO medical_records (patient_id, doctor_id, diagnosis, prescription, date) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, Integer.parseInt(patientIdField.getText().trim()));
            ps.setInt(2, doctorId);
            ps.setString(3, diagnosisField.getText().trim());
            ps.setString(4, prescriptionField.getText().trim());
            ps.setDate(5, Date.valueOf(dateField.getText().trim()));
            ps.executeUpdate();
            loadMyRecords();
            JOptionPane.showMessageDialog(this, "Record added successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding record: " + ex.getMessage());
        }
    }

    // ✅ Update record
    private void updateRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to update.");
            return;
        }

        int recordId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE medical_records SET patient_id=?, diagnosis=?, prescription=?, date=? WHERE record_id=? AND doctor_id=?")) {
            ps.setInt(1, Integer.parseInt(patientIdField.getText().trim()));
            ps.setString(2, diagnosisField.getText().trim());
            ps.setString(3, prescriptionField.getText().trim());
            ps.setDate(4, Date.valueOf(dateField.getText().trim()));
            ps.setInt(5, recordId);
            ps.setInt(6, doctorId);
            ps.executeUpdate();
            loadMyRecords();
            JOptionPane.showMessageDialog(this, "Record updated successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating record: " + ex.getMessage());
        }
    }

    // ✅ Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorRecordsFrame(1)); // test with doctorId=1
    }
}