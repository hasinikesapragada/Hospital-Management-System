package project1.patient;

import javax.swing.*;
import java.sql.*;

public class PatientRegisterFrame extends JFrame {
    private JTextField nameField, ageField, genderField, contactField, emailField, usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;

    public PatientRegisterFrame() {
        setTitle("Patient Registration");
        setSize(400, 400);
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

        JLabel contactLabel = new JLabel("Contact:");
        contactLabel.setBounds(30, 150, 100, 30);
        add(contactLabel);
        contactField = new JTextField();
        contactField.setBounds(150, 150, 200, 30);
        add(contactField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(30, 190, 100, 30);
        add(emailLabel);
        emailField = new JTextField();
        emailField.setBounds(150, 190, 200, 30);
        add(emailField);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(30, 230, 100, 30);
        add(usernameLabel);
        usernameField = new JTextField();
        usernameField.setBounds(150, 230, 200, 30);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 270, 100, 30);
        add(passwordLabel);
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 270, 200, 30);
        add(passwordField);

        registerButton = new JButton("Register");
        registerButton.setBounds(150, 320, 100, 30);
        add(registerButton);

        registerButton.addActionListener(e -> registerPatient());

        setVisible(true);
    }

    // ✅ Database connection
    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
    }

    // ✅ Registration logic
    private void registerPatient() {
        try (Connection conn = connect()) {
            conn.setAutoCommit(false); // transaction

            // Insert into patients table
            String insertPatient = "INSERT INTO patients (name, age, gender, contact, email) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psPatient = conn.prepareStatement(insertPatient, Statement.RETURN_GENERATED_KEYS);
            psPatient.setString(1, nameField.getText().trim());
            psPatient.setInt(2, Integer.parseInt(ageField.getText().trim()));
            psPatient.setString(3, genderField.getText().trim());
            psPatient.setString(4, contactField.getText().trim());
            psPatient.setString(5, emailField.getText().trim());
            psPatient.executeUpdate();

            ResultSet rs = psPatient.getGeneratedKeys();
            int patientId = -1;
            if (rs.next()) {
                patientId = rs.getInt(1);
            }

            // Insert into users table
            String insertUser = "INSERT INTO users (username, password, role, patient_id) VALUES (?, ?, ?, ?)";
            PreparedStatement psUser = conn.prepareStatement(insertUser);
            psUser.setString(1, usernameField.getText().trim());
            psUser.setString(2, new String(passwordField.getPassword()));
            psUser.setString(3, "patient");
            psUser.setInt(4, patientId);
            psUser.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Registration successful! Please log in.");
            dispose(); // close registration window
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PatientRegisterFrame::new);
    }
}