package project1.patient;

import javax.swing.*;
import java.sql.*;

public class PatientProfileFrame extends JFrame {
    private int patientId;
    private JTextField nameField, ageField, genderField, phoneField, emailField;

    public PatientProfileFrame(int patientId) {
        this.patientId = patientId;
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

        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setBounds(30, 70, 100, 30);
        add(ageLabel);

        ageField = new JTextField();
        ageField.setBounds(150, 70, 200, 30);
        add(ageField);

        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(30, 110, 100, 30);
        add(genderLabel);

        genderField = new JTextField();
        genderField.setBounds(150, 110, 200, 30);
        add(genderField);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(30, 150, 100, 30);
        add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(150, 150, 200, 30);
        add(phoneField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(30, 190, 100, 30);
        add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 190, 200, 30);
        add(emailField);

        JButton updateButton = new JButton("Update Profile");
        updateButton.setBounds(150, 240, 150, 30);
        add(updateButton);

        // Load patient details
        loadProfile();

        // Update action
        updateButton.addActionListener(e -> updateProfile());

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
    }

    // ✅ Load patient profile
    private void loadProfile() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients WHERE patient_id=?")) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                ageField.setText(String.valueOf(rs.getInt("age")));
                genderField.setText(rs.getString("gender"));
                phoneField.setText(rs.getString("contact"));
                emailField.setText(rs.getString("email"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage());
        }
    }

    // ✅ Update patient profile
    private void updateProfile() {
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE patients SET name=?, age=?, gender=?, contact=?, email=? WHERE patient_id=?")) {
            ps.setString(1, nameField.getText().trim());
            ps.setInt(2, Integer.parseInt(ageField.getText().trim()));
            ps.setString(3, genderField.getText().trim());
            ps.setString(4, phoneField.getText().trim());
            ps.setString(5, emailField.getText().trim());
            ps.setInt(6, patientId);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + ex.getMessage());
        }
    }

    // ✅ Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientProfileFrame(101)); // test with patientId=101
    }
}