package project1.admin;

import project1.ui.UITheme;
import project1.login.Login;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Dashboard extends JFrame {

    // Define two main colors
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80);   // dark gray/navy
    private static final Color SECONDARY_COLOR = new Color(245, 245, 245); // light gray/white

    public Dashboard() {
        setTitle("Hospital Management System - Admin Dashboard");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Sidebar Navigation ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS)); // vertical layout
        sidebar.setBackground(PRIMARY_COLOR);
        sidebar.setPreferredSize(new Dimension(220, 650));

        // Buttons (all consistent theme)
        JButton patientsBtn = createNavButton("Patients", "icons/patients.png");
        patientsBtn.addActionListener(e -> new PatientFrame());

        JButton doctorsBtn = createNavButton("Doctors", "icons/doctors.png");
        doctorsBtn.addActionListener(e -> new DoctorFrame());

        JButton nursesBtn = createNavButton("Nurses", "icons/nurses.png");
        nursesBtn.addActionListener(e -> new NurseRecordsFrame());

        JButton appointmentsBtn = createNavButton("Appointments", "icons/appointments.png");
        appointmentsBtn.addActionListener(e -> new AppointmentFrame());

        JButton billingBtn = createNavButton("Billing", "icons/billing.png");
        billingBtn.addActionListener(e -> new BillingFrame());

        JButton usersBtn = createNavButton("Users", "icons/users.png");
        usersBtn.addActionListener(e -> new UsersFrame());

        JButton recordsBtn = createNavButton("Medical Records", "icons/records.png");
        recordsBtn.addActionListener(e -> new MedicalRecordsFrame());

        JButton roomsBtn = createNavButton("Rooms", "icons/rooms.png");
        roomsBtn.addActionListener(e -> new RoomsFrame());

        JButton logoutBtn = createNavButton("Logout", "icons/logout.png");
        logoutBtn.setBackground(new Color(231, 76, 60)); // red for logout
        logoutBtn.addActionListener(e -> {
            dispose();
            new Login();
        });

        // Add buttons neatly stacked with spacing
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(patientsBtn);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(doctorsBtn);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(nursesBtn);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(appointmentsBtn);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(billingBtn);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(usersBtn);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(recordsBtn);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(roomsBtn);
        sidebar.add(Box.createVerticalGlue()); // pushes logout to bottom
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(logoutBtn);
        sidebar.add(Box.createVerticalStrut(12));

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(1100, 60));
        JLabel title = new JLabel("🏥 Hospital Management System - Admin Dashboard", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        // --- Dashboard Cards ---
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        cardsPanel.add(createCard("Total Patients", getCount("patients"), SECONDARY_COLOR, "icons/patients.png"));
        cardsPanel.add(createCard("Active Doctors", getCount("doctors"), SECONDARY_COLOR, "icons/doctors.png"));
        cardsPanel.add(createCard("Pending Appointments", getCount("appointments WHERE status='Pending'"), SECONDARY_COLOR, "icons/appointments.png"));
        cardsPanel.add(createCard("Available Rooms", getCount("rooms WHERE status='Available'"), SECONDARY_COLOR, "icons/rooms.png"));

        // --- Add panels to frame ---
        add(sidebar, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
        add(cardsPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    // --- Create a styled navigation button ---
    private JButton createNavButton(String text, String iconPath) {
        JButton button = new JButton(text, new ImageIcon(iconPath));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT); // align text + icon left
        button.setIconTextGap(15);                          // spacing between icon and text
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);     // align with sidebar
        button.setMaximumSize(new Dimension(220, 45));      // uniform size
        return button;
    }

    // --- Create a styled card ---
    private JPanel createCard(String title, String value, Color bgColor, String iconPath) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel iconLabel = new JLabel(new ImageIcon(iconPath), JLabel.CENTER);

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.BLACK);

        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(Color.BLACK);

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(titleLabel, BorderLayout.CENTER);
        card.add(valueLabel, BorderLayout.SOUTH);

        card.setPreferredSize(new Dimension(200, 150));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        return card;
    }

    // --- Get counts from DB ---
    private String getCount(String table) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital", "root", "1234");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM " + table)) {
            if (rs.next()) {
                return String.valueOf(rs.getInt("count"));
            }
        } catch (SQLException e) {
            return "Error";
        }
        return "0";
    }

    // --- Main method ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Dashboard::new);
    }
}