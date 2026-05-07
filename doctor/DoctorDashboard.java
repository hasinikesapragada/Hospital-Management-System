package project1.doctor;

import javax.swing.*;

import project1.login.Login;

import java.awt.*;

public class DoctorDashboard extends JFrame {
    private int doctorId;

    public DoctorDashboard(int doctorId) {
        this.doctorId = doctorId;
        setTitle("Doctor Portal");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton appointmentsBtn = new JButton("My Appointments");
        appointmentsBtn.addActionListener(e -> new DoctorAppointmentsFrame(doctorId));


        JButton recordsBtn = new JButton("My Patients' Records");
        recordsBtn.addActionListener(e -> new DoctorRecordsFrame(doctorId));


        JButton profileBtn = new JButton("My Profile");
        profileBtn.addActionListener(e -> new DoctorProfileFrame(doctorId));

        JButton btnSlots = new JButton("My Slots");
        btnSlots.setBounds(50, 200, 150, 40); // adjust position as needed
        add(btnSlots);

        // Action listener to open DoctorSlotsFrame
        btnSlots.addActionListener(e -> new DoctorSlotsFrame());


        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new Login(); // back to login
        });

        setLayout(new FlowLayout());
        add(appointmentsBtn);
        add(recordsBtn);
        add(profileBtn);
        add(logoutBtn);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorDashboard(201)); // test with doctorId=201
    }
}