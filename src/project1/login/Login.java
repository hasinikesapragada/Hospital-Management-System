package project1.login;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

import project1.admin.Dashboard;
import project1.doctor.DoctorDashboard;
import project1.patient.PatientDashboard;
import project1.patient.PatientRegisterFrame;
import project1.nurse.NurseDashboard;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public Login() {
        setTitle("Hospital Management System - Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // --- Header ---
        JLabel header = new JLabel("🏥 Welcome to HMS", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 22));
        header.setBounds(50, 10, 350, 40);
        header.setForeground(new Color(52, 73, 94));
        add(header);

        // --- Username ---
        JLabel usernameLabel = new JLabel("👤 Username:");
        usernameLabel.setBounds(50, 70, 100, 30);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(160, 70, 200, 30);
        add(usernameField);

        // --- Password ---
        JLabel passwordLabel = new JLabel("🔒 Password:");
        passwordLabel.setBounds(50, 120, 100, 30);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(160, 120, 200, 30);
        add(passwordField);

        // --- Buttons ---
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(160, 170, 100, 35);
        loginButton.setBackground(new Color(46, 204, 113));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        add(loginButton);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(270, 170, 100, 35);
        registerButton.setBackground(new Color(52, 152, 219));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        add(registerButton);

        // --- Footer ---
        JLabel footer = new JLabel("Powered by Hospital Management System", JLabel.CENTER);
        footer.setBounds(50, 250, 350, 30);
        footer.setFont(new Font("Arial", Font.ITALIC, 12));
        footer.setForeground(new Color(127, 140, 141));
        add(footer);

        // Actions
        loginButton.addActionListener(e -> authenticate());
        registerButton.addActionListener(e -> {
            dispose();
            new PatientRegisterFrame();
        });

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hospital";
        String user = "root";
        String password = "1234";
        return DriverManager.getConnection(url, user, password);
    }

    // ✅ Authentication method
    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter both username and password!");
            return;
        }

        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                JOptionPane.showMessageDialog(this, "Login successful! Role: " + role);
                dispose();

                if (role.equalsIgnoreCase("admin")) {
                    new Dashboard();
                } else if (role.equalsIgnoreCase("doctor")) {
                    int doctorId = rs.getInt("doctor_id");
                    new DoctorDashboard(doctorId);
                } else if (role.equalsIgnoreCase("receptionist")) {
                    new Dashboard();
                } else if (role.equalsIgnoreCase("patient")) {
                    int patientId = rs.getInt("patient_id");
                    new PatientDashboard(patientId);
                } else if (role.equalsIgnoreCase("nurse")) {
                    int userId = rs.getInt("user_id");
                    try (PreparedStatement ps2 = conn.prepareStatement("SELECT nurse_id FROM Nurses WHERE user_id=?")) {
                        ps2.setInt(1, userId);
                        ResultSet rs2 = ps2.executeQuery();
                        if (rs2.next()) {
                            int nurseId = rs2.getInt("nurse_id");
                            new NurseDashboard(nurseId);
                        } else {
                            JOptionPane.showMessageDialog(this, "No nurse profile found for this user!");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Unknown role: " + role);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error during login: " + ex.getMessage());
        }
    }

    // ✅ Main method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}