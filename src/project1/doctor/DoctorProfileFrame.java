package project1.doctor;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DoctorProfileFrame extends JFrame {
    private int doctorId;
    private JTextField nameField, specializationField, phoneField, emailField;

    public DoctorProfileFrame(int doctorId) {
        this.doctorId = doctorId;
        setTitle("Doctor Portal - My Profile");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setPreferredSize(new Dimension(600, 60));
        JLabel title = new JLabel("👨‍⚕️ My Profile", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        formPanel.setBackground(new Color(236, 240, 241));

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameField = new JTextField();

        JLabel specializationLabel = new JLabel("Specialization:");
        specializationLabel.setFont(new Font("Arial", Font.BOLD, 14));
        specializationField = new JTextField();

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        phoneField = new JTextField();

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailField = new JTextField();

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(specializationLabel);
        formPanel.add(specializationField);
        formPanel.add(phoneLabel);
        formPanel.add(phoneField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(236, 240, 241));

        JButton updateButton = new JButton("Update Profile");
        updateButton.setBackground(new Color(46, 204, 113));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFont(new Font("Arial", Font.BOLD, 14));
        updateButton.setFocusPainted(false);
        updateButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        updateButton.addActionListener(e -> updateProfile());

        buttonPanel.add(updateButton);

        // --- Add panels ---
        add(header, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load doctor details
        loadProfile();

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
                     "UPDATE doctors SET name=?, specialization=?, contact=?, email=? WHERE doctor_id=?")) {
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