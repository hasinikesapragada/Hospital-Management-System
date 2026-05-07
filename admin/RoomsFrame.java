package project1.admin;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class RoomsFrame extends JFrame {
    private JTextField roomTypeField, statusField, chargesField;
    private JTable table;
    private DefaultTableModel tableModel;

    public RoomsFrame() {
        setTitle("Rooms Management");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel roomTypeLabel = new JLabel("Room Type:");
        roomTypeLabel.setBounds(30, 30, 100, 30);
        add(roomTypeLabel);

        roomTypeField = new JTextField();
        roomTypeField.setBounds(150, 30, 150, 30);
        add(roomTypeField);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(30, 70, 100, 30);
        add(statusLabel);

        statusField = new JTextField();
        statusField.setBounds(150, 70, 150, 30);
        add(statusField);

        JLabel chargesLabel = new JLabel("Charges/Day:");
        chargesLabel.setBounds(30, 110, 100, 30);
        add(chargesLabel);

        chargesField = new JTextField();
        chargesField.setBounds(150, 110, 150, 30);
        add(chargesField);

        JButton addButton = new JButton("Add");
        addButton.setBounds(30, 160, 100, 30);
        add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(140, 160, 100, 30);
        add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(250, 160, 100, 30);
        add(deleteButton);

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"Room ID", "Room Type", "Status", "Charges/Day"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 210, 620, 120);
        add(scrollPane);

        // Load rooms
        loadRooms();

        // Button actions
        addButton.addActionListener(e -> addRoom());
        updateButton.addActionListener(e -> updateRoom());
        deleteButton.addActionListener(e -> deleteRoom());

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hospital"; 
        String user = "root"; 
        String password = "1234"; 
        return DriverManager.getConnection(url, user, password);
    }

    // ✅ Load rooms
    private void loadRooms() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM rooms")) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("room_id"),
                        rs.getString("room_type"),
                        rs.getString("status"),
                        rs.getBigDecimal("charges")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + e.getMessage());
        }
    }

    // ✅ Add room
    private void addRoom() {
        String roomType = roomTypeField.getText().trim();
        String status = statusField.getText().trim();
        String charges = chargesField.getText().trim();

        if (roomType.isEmpty() || status.isEmpty() || charges.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled!");
            return;
        }

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO rooms (room_type, status, charges) VALUES (?, ?, ?)")) {
            ps.setString(1, roomType);
            ps.setString(2, status);
            ps.setBigDecimal(3, new java.math.BigDecimal(charges));
            ps.executeUpdate();
            loadRooms();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding room: " + ex.getMessage());
        }
    }

    // ✅ Update room
    private void updateRoom() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a room to update.");
            return;
        }

        int roomId = (int) tableModel.getValueAt(selectedRow, 0);
        String roomType = roomTypeField.getText().trim();
        String status = statusField.getText().trim();
        String charges = chargesField.getText().trim();

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE rooms SET room_type=?, status=?, charges=? WHERE room_id=?")) {
            ps.setString(1, roomType);
            ps.setString(2, status);
            ps.setBigDecimal(3, new java.math.BigDecimal(charges));
            ps.setInt(4, roomId);
            ps.executeUpdate();
            loadRooms();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating room: " + ex.getMessage());
        }
    }

    // ✅ Delete room
    private void deleteRoom() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a room to delete.");
            return;
        }

        int roomId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM rooms WHERE room_id=?")) {
            ps.setInt(1, roomId);
            ps.executeUpdate();
            loadRooms();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting room: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RoomsFrame());
    }
}