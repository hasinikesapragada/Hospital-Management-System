package project1.patient;

import javax.swing.*;

import project1.login.Login;

import java.awt.*;

public class PatientDashboard extends JFrame {
    private int patientId;

    public PatientDashboard(int patientId) {
        this.patientId = patientId;
        setTitle("Patient Portal");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use your existing frames
        JButton reportsBtn = new JButton("View My Reports");
        reportsBtn.addActionListener(e -> new PatientReportsFrame(patientId));
// you already have MedicalRecordsFrame

        JButton appointmentBtn = new JButton("Book Appointment");
        appointmentBtn.addActionListener(e -> new PatientAppointmentFrame(patientId));


        JButton billingBtn = new JButton("View Billing History");
        billingBtn.addActionListener(e -> new PatientBillingFrame(patientId));
// already have BillingFrame

        JButton profileBtn = new JButton("My Profile");
        profileBtn.addActionListener(e -> new PatientProfileFrame(patientId));
 // you already have PatientFrame

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new Login(); // back to login
        });

        setLayout(new FlowLayout());
        add(reportsBtn);
        add(appointmentBtn);
        add(billingBtn);
        add(profileBtn);
        add(logoutBtn);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PatientDashboard(101)); // test with patientId=101
    }
}