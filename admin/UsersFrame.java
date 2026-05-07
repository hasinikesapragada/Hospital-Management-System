package project1.admin;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class UsersFrame extends JFrame {
    private JTextField usernameField, roleField;
    private JPasswordField passwordField;
    private JTable table;
    private DefaultTableModel tableModel;

    public UsersFrame() {
        setTitle("User Management");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(30, 30, 100, 30);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 30, 150, 30);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 70, 100, 30);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 70, 150, 30);
        add(passwordField);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setBounds(30, 110, 100, 30);
        add(roleLabel);

        roleField = new JTextField();
        roleField.setBounds(150, 110, 150, 30);
        add(roleField);

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
        tableModel = new DefaultTableModel(new String[]{"User ID", "Username", "Role"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 210, 620, 120);
        add(scrollPane);

        // Load users
        loadUsers();

        // Button actions
        addButton.addActionListener(e -> addUser());
        updateButton.addActionListener(e -> updateUser());
        deleteButton.addActionListener(e -> deleteUser());

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hospital"; // replace with your DB name
        String user = "root"; // replace with your MySQL username
        String password = "1234"; // replace with your MySQL password
        return DriverManager.getConnection(url, user, password);
    }

    // ✅ Load users
    private void loadUsers() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            tableModel.setRowCount(0);
            while (rs.next()) {
                Object[] row = {
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("role")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }

    // ✅ Add user
    private void addUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = roleField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled!");
            return;
        }

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO users (username, password, role) VALUES (?, ?, ?)")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.executeUpdate();
            loadUsers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error adding user: " + ex.getMessage());
        }
    }

    // ✅ Update user
    private void updateUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to update.");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = roleField.getText().trim();

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE users SET username=?, password=?, role=? WHERE user_id=?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setInt(4, userId);
            ps.executeUpdate();
            loadUsers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating user: " + ex.getMessage());
        }
    }

    // ✅ Delete user
    private void deleteUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to delete.");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE user_id=?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            loadUsers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UsersFrame());
    }
}