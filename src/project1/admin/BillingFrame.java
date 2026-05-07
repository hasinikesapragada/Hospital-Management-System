package project1.admin;

import project1.ui.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BillingFrame extends JFrame {
    private JTextField patientIdField, amountField, dateField, searchField;
    private JTable table;
    private DefaultTableModel tableModel;

    public BillingFrame() {
        setTitle("Billing Management");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // --- Search Section ---
        JLabel searchLabel = new JLabel("Search by Patient ID:");
        searchLabel.setBounds(30, 20, 150, 30);
        UITheme.styleLabel(searchLabel);
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(180, 20, 150, 30);
        add(searchField);

        JButton searchButton = new JButton("Search");
        searchButton.setBounds(350, 20, 100, 30);
        UITheme.styleButton(searchButton, new Color(241, 196, 15), Color.BLACK); // Yellow
        add(searchButton);

        // --- Input Fields ---
        JLabel patientIdLabel = new JLabel("Patient ID:");
        patientIdLabel.setBounds(30, 70, 100, 30);
        UITheme.styleLabel(patientIdLabel);
        add(patientIdLabel);

        patientIdField = new JTextField();
        patientIdField.setBounds(150, 70, 150, 30);
        add(patientIdField);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(30, 110, 100, 30);
        UITheme.styleLabel(amountLabel);
        add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(150, 110, 150, 30);
        add(amountField);

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setBounds(30, 150, 150, 30);
        UITheme.styleLabel(dateLabel);
        add(dateLabel);

        dateField = new JTextField();
        dateField.setBounds(180, 150, 150, 30);
        add(dateField);

        // --- Buttons ---
        JButton addButton = new JButton("Add");
        addButton.setBounds(30, 200, 100, 30);
        UITheme.styleButton(addButton, new Color(46, 204, 113), Color.WHITE); // Green
        add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(140, 200, 100, 30);
        UITheme.styleButton(updateButton, new Color(52, 152, 219), Color.WHITE); // Blue
        add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(250, 200, 100, 30);
        UITheme.styleButton(deleteButton, new Color(231, 76, 60), Color.WHITE); // Red
        add(deleteButton);

        // --- Table ---
        tableModel = new DefaultTableModel(new String[]{"Bill ID", "Patient ID", "Amount", "Date"}, 0);
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(400, 70, 460, 300);
        add(scrollPane);

        // --- Load billing records initially ---
        loadBilling();

        // --- Button Actions ---
        addButton.addActionListener(e -> addBilling());
        updateButton.addActionListener(e -> updateBilling());
        deleteButton.addActionListener(e -> deleteBilling());
        searchButton.addActionListener(e -> searchBilling());

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hospital";
        String user = "root";
        String password = "1234";
        return DriverManager.getConnection(url, user, password);
    }

    // ✅ Load all billing records
    private void loadBilling() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM billing")) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("bill_id"),
                        rs.getInt("patient_id"),
                        rs.getBigDecimal("amount"),
                        rs.getDate("date")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading billing records: " + e.getMessage());
        }
    }

    // ✅ Search billing records by patient_id
    private void searchBilling() {
        String patientId = searchField.getText().trim();
        if (patientId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Patient ID!");
            return;
        }

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM billing WHERE patient_id=?")) {
            ps.setInt(1, Integer.parseInt(patientId));
            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("bill_id"),
                        rs.getInt("patient_id"),
                        rs.getBigDecimal("amount"),
                        rs.getDate("date")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error searching billing records: " + ex.getMessage());
        }
    }

    // ✅ Add billing record
    private void addBilling() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO billing (patient_id, amount, date) VALUES (?, ?, ?)")) {
            ps.setInt(1, Integer.parseInt(patientIdField.getText().trim()));
            ps.setBigDecimal(2, new java.math.BigDecimal(amountField.getText().trim()));
            ps.setDate(3, Date.valueOf(dateField.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Billing record added successfully!");
            loadBilling();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding billing record: " + ex.getMessage());
        }
    }

    // ✅ Update billing record
    private void updateBilling() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a billing record to update.");
            return;
        }

        int billId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE billing SET patient_id=?, amount=?, date=? WHERE bill_id=?")) {
            ps.setInt(1, Integer.parseInt(patientIdField.getText().trim()));
            ps.setBigDecimal(2, new java.math.BigDecimal(amountField.getText().trim()));
            ps.setDate(3, Date.valueOf(dateField.getText().trim()));
            ps.setInt(4, billId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Billing record updated successfully!");
            loadBilling();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating billing record: " + ex.getMessage());
        }
    }

    // ✅ Delete billing record
    private void deleteBilling() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a billing record to delete.");
            return;
        }

        int billId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM billing WHERE bill_id=?")) {
            ps.setInt(1, billId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Billing record deleted successfully!");
            loadBilling();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting billing record: " + ex.getMessage());
        }
    }

    // ✅ Main method to run independently
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BillingFrame::new);
    }
}