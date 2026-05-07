package project1.doctor;

import javax.swing.*;
import java.sql.*;

public class DoctorProfileFrame extends JFrame {
    private int doctorId;
    private JTextField nameField, specializationField, phoneField, emailField;

    public DoctorProfileFrame(int doctorId) {
        this.doctorId = doctorId;
        setTitle("My Profile");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(30, 30, 100, 30);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(150, 30, 200, 30);
        add(nameField);

        JLabel specializationLabel = new JLabel("Specialization:");
        specializationLabel.setBounds(30, 70, 100, 30);
        add(specializationLabel);

        specializationField = new JTextField();
        specializationField.setBounds(150, 70, 200, 30);
        add(specializationField);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(30, 110, 100, 30);
        add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(150, 110, 200, 30);
        add(phoneField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(30, 150, 100, 30);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 150, 200, 30);
        add(emailField);

        JButton updateButton = new JButton("Update Profile");
        updateButton.setBounds(150, 200, 150, 30);
        add(updateButton);

        // Load doctor details
        loadProfile();

        // Update action
        updateButton.addActionListener(e -> updateProfile());

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
    }

    // ✅ Load doctor profile
    private void loadProfile() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM doctors WHERE doctor_id=?")) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                specializationField.setText(rs.getString("specialization"));
                phoneField.setText(rs.getString("contact"));
                emailField.setText(rs.getString("email"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage());
        }
    }

    // ✅ Update doctor profile
    private void updateProfile() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE doctors SET name=?, specialization=?, phone=?, email=? WHERE doctor_id=?")) {
            ps.setString(1, nameField.getText().trim());
            ps.setString(2, specializationField.getText().trim());
            ps.setString(3, phoneField.getText().trim());
            ps.setString(4, emailField.getText().trim());
            ps.setInt(5, doctorId);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + ex.getMessage());
        }
    }

    // ✅ Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorProfileFrame(201)); // test with doctorId=201
    }
}