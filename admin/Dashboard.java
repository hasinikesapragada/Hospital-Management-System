package project1.admin;

import javax.swing.*;

public class Dashboard extends JFrame {

    public Dashboard(String role) {
        setTitle("Hospital Management System - Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();

        // Patients menu
        JMenu patientMenu = new JMenu("Patients");
        JMenuItem managePatients = new JMenuItem("Manage Patients");
        managePatients.addActionListener(e -> new PatientFrame());
        patientMenu.add(managePatients);

        // Doctors menu
        JMenu doctorMenu = new JMenu("Doctors");
        JMenuItem manageDoctors = new JMenuItem("Manage Doctors");
        manageDoctors.addActionListener(e -> new DoctorFrame());
        doctorMenu.add(manageDoctors);

        // Appointments menu
        JMenu appointmentMenu = new JMenu("Appointments");
        JMenuItem manageAppointments = new JMenuItem("Manage Appointments");
        manageAppointments.addActionListener(e -> new AppointmentFrame());
        appointmentMenu.add(manageAppointments);

        // Billing menu
        JMenu billingMenu = new JMenu("Billing");
        JMenuItem manageBilling = new JMenuItem("Manage Billing");
        manageBilling.addActionListener(e -> new BillingFrame());
        billingMenu.add(manageBilling);

        // Users menu
        JMenu usersMenu = new JMenu("Users");
        JMenuItem manageUsers = new JMenuItem("Manage Users");
        manageUsers.addActionListener(e -> new UsersFrame());
        usersMenu.add(manageUsers);

        // Medical Records menu
        JMenu recordsMenu = new JMenu("Medical Records");
        JMenuItem manageRecords = new JMenuItem("Manage Records");
        manageRecords.addActionListener(e -> new MedicalRecordsFrame());
        recordsMenu.add(manageRecords);

        // Rooms menu
        JMenu roomsMenu = new JMenu("Rooms");
        JMenuItem manageRooms = new JMenuItem("Manage Rooms");
        manageRooms.addActionListener(e -> new RoomsFrame());
        roomsMenu.add(manageRooms);

        // Admissions menu
        JMenu admissionsMenu = new JMenu("Admissions");
        JMenuItem manageAdmissions = new JMenuItem("Manage Admissions");
        manageAdmissions.addActionListener(e -> new AdmissionsFrame());
        admissionsMenu.add(manageAdmissions);

        // 🔹 Role-based access control
        if (role.equalsIgnoreCase("admin")) {
            menuBar.add(patientMenu);
            menuBar.add(doctorMenu);
            menuBar.add(appointmentMenu);
            menuBar.add(billingMenu);
            menuBar.add(usersMenu);
            menuBar.add(recordsMenu);
            menuBar.add(roomsMenu);
            menuBar.add(admissionsMenu);
        } else if (role.equalsIgnoreCase("doctor")) {
            menuBar.add(patientMenu);
            menuBar.add(doctorMenu);
            menuBar.add(recordsMenu);
        } else if (role.equalsIgnoreCase("receptionist")) {
            menuBar.add(patientMenu);
            menuBar.add(appointmentMenu);
            menuBar.add(billingMenu);
            menuBar.add(admissionsMenu);
        } else {
            JOptionPane.showMessageDialog(this, "Unknown role: " + role);
        }

        setJMenuBar(menuBar);

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome to Hospital Management System", SwingConstants.CENTER);
        welcomeLabel.setBounds(100, 200, 600, 50);
        add(welcomeLabel);

        setLayout(null);
        setVisible(true);
    }

    // For testing only: run dashboard directly with a role
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Dashboard("admin"));
    }
}