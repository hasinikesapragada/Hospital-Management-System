package project1.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class AdmissionsFrame extends JFrame {
    private JTextField patientIdField, roomIdField, admissionDateField, dischargeDateField, searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public AdmissionsFrame() {
        setTitle("Admissions Management");
        setSize(900, 500);
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

        // --- Input Fields ---
        JLabel patientIdLabel = new JLabel("Patient ID:");
        patientIdLabel.setBounds(30, 70, 100, 30);
        add(patientIdLabel);

        patientIdField = new JTextField();
        patientIdField.setBounds(150, 70, 150, 30);
        add(patientIdField);

        JLabel roomIdLabel = new JLabel("Room ID:");
        roomIdLabel.setBounds(30, 110, 100, 30);
        add(roomIdLabel);

        roomIdField = new JTextField();
        roomIdField.setBounds(150, 110, 150, 30);
        add(roomIdField);

        JLabel admissionDateLabel = new JLabel("Admission Date (YYYY-MM-DD):");
        admissionDateLabel.setBounds(30, 150, 200, 30);
        add(admissionDateLabel);

        admissionDateField = new JTextField();
        admissionDateField.setBounds(230, 150, 150, 30);
        add(admissionDateField);

        JLabel dischargeDateLabel = new JLabel("Discharge Date (YYYY-MM-DD):");
        dischargeDateLabel.setBounds(30, 190, 200, 30);
        add(dischargeDateLabel);

        dischargeDateField = new JTextField();
        dischargeDateField.setBounds(230, 190, 150, 30);
        add(dischargeDateField);

        // --- Buttons ---
        JButton addButton = new JButton("Add");
        addButton.setBounds(30, 230, 100, 30);
        add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(140, 230, 100, 30);
        add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(250, 230, 100, 30);
        add(deleteButton);

        // --- Table ---
        tableModel = new DefaultTableModel(new String[]{"Admission ID", "Patient ID", "Room ID", "Admission Date", "Discharge Date"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(420, 70, 450, 300);
        add(scrollPane);

        // --- Load Admissions Initially ---
        loadAdmissions();

        // --- Button Actions ---
        addButton.addActionListener(e -> addAdmission());
        updateButton.addActionListener(e -> updateAdmission());
        deleteButton.addActionListener(e -> deleteAdmission());
        searchButton.addActionListener(e -> searchAdmissions());

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hospital";
        String user = "root";
        String password = "1234";
        return DriverManager.getConnection(url, user, password);
    }

    // ✅ Load all admissions
    private void loadAdmissions() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM admissions")) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("admission_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("room_id"),
                        rs.getDate("admission_date"),
                        rs.getDate("discharge_date")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading admissions: " + e.getMessage());
        }
    }

    // ✅ Search admissions by patient_id
    private void searchAdmissions() {
        String patientId = searchField.getText().trim();
        if (patientId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Patient ID!");
            return;
        }

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM admissions WHERE patient_id=?")) {
            ps.setInt(1, Integer.parseInt(patientId));
            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("admission_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("room_id"),
                        rs.getDate("admission_date"),
                        rs.getDate("discharge_date")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error searching admissions: " + ex.getMessage());
        }
    }

    // ✅ Add admission
    private void addAdmission() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO admissions (patient_id, room_id, admission_date, discharge_date) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, Integer.parseInt(patientIdField.getText().trim()));
            ps.setInt(2, Integer.parseInt(roomIdField.getText().trim()));
            ps.setDate(3, Date.valueOf(admissionDateField.getText().trim()));
            ps.setDate(4, Date.valueOf(dischargeDateField.getText().trim()));
            ps.executeUpdate();
            loadAdmissions();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding admission: " + ex.getMessage());
        }
    }

    // ✅ Update admission
    private void updateAdmission() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an admission to update.");
            return;
        }

        int admissionId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE admissions SET patient_id=?, room_id=?, admission_date=?, discharge_date=? WHERE admission_id=?")) {
            ps.setInt(1, Integer.parseInt(patientIdField.getText().trim()));
            ps.setInt(2, Integer.parseInt(roomIdField.getText().trim()));
            ps.setDate(3, Date.valueOf(admissionDateField.getText().trim()));
            ps.setDate(4, Date.valueOf(dischargeDateField.getText().trim()));
            ps.setInt(5, admissionId);
            ps.executeUpdate();
            loadAdmissions();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating admission: " + ex.getMessage());
        }
    }

    // ✅ Delete admission
    private void deleteAdmission() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an admission to delete.");
            return;
        }

        int admissionId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM admissions WHERE admission_id=?")) {
            ps.setInt(1, admissionId);
            ps.executeUpdate();
            loadAdmissions();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting admission: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdmissionsFrame());
    }
}