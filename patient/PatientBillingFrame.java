package project1.patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class PatientBillingFrame extends JFrame {
    private int patientId;
    private JTable table;
    private DefaultTableModel tableModel;

    public PatientBillingFrame(int patientId) {
        this.patientId = patientId;
        setTitle("My Billing History");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(new String[]{"Bill ID", "Amount", "Date"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        loadMyBilling();

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hospital";
        String user = "root";
        String password = "1234";
        return DriverManager.getConnection(url, user, password);
    }

    // ✅ Load billing records only for this patient
    private void loadMyBilling() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT bill_id, amount, date FROM billing WHERE patient_id=?")) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("bill_id"),
                        rs.getBigDecimal("amount"),
                        rs.getDate("date")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading billing: " + e.getMessage());
        }
    }

    // ✅ Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientBillingFrame(101)); // test with patientId=101
    }
}