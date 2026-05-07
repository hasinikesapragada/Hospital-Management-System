package project1.doctor;

import javax.swing.*;
import javax.swing.JOptionPane;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import project1.doctor.Slot;
import project1.doctor.SlotService;

public class DoctorSlotsFrame extends JFrame {
    private JTextField txtDoctorId, txtStartTime, txtEndTime, txtWeekStart, txtWeekEnd;
    private JButton btnAddSlot, btnViewSlots, btnUpdateSlot;

    private SlotService slotService = new SlotService();

    public DoctorSlotsFrame() {
        setTitle("Doctor Portal - Weekly Slots");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(52, 73, 94));
        header.setPreferredSize(new Dimension(600, 60));
        JLabel title = new JLabel("⏰ Manage Weekly Slots", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        // Form
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        formPanel.setBackground(new Color(236, 240, 241));

        txtDoctorId = new JTextField();
        txtStartTime = new JTextField();
        txtEndTime = new JTextField();
        txtWeekStart = new JTextField();
        txtWeekEnd = new JTextField();

        formPanel.add(new JLabel("Doctor ID:"));
        formPanel.add(txtDoctorId);
        formPanel.add(new JLabel("Start Time (yyyy-MM-dd HH:mm):"));
        formPanel.add(txtStartTime);
        formPanel.add(new JLabel("End Time (yyyy-MM-dd HH:mm):"));
        formPanel.add(txtEndTime);
        formPanel.add(new JLabel("Week Start (yyyy-MM-dd):"));
        formPanel.add(txtWeekStart);
        formPanel.add(new JLabel("Week End (yyyy-MM-dd):"));
        formPanel.add(txtWeekEnd);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(236, 240, 241));

        btnAddSlot = new JButton("Add Slot");
        styleButton(btnAddSlot, new Color(46, 204, 113), Color.WHITE);

        btnViewSlots = new JButton("View Slots");
        styleButton(btnViewSlots, new Color(52, 152, 219), Color.WHITE);

        btnUpdateSlot = new JButton("Update Slot");
        styleButton(btnUpdateSlot, new Color(241, 196, 15), Color.BLACK);

        buttonPanel.add(btnAddSlot);
        buttonPanel.add(btnViewSlots);
        buttonPanel.add(btnUpdateSlot);

        add(header, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        btnAddSlot.addActionListener(e -> addSlot());
        btnViewSlots.addActionListener(e -> viewSlots());
        btnUpdateSlot.addActionListener(e -> updateSlot());

        setVisible(true);
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private boolean isEmpty(JTextField field, String fieldName) {
        if (field.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, fieldName + " cannot be empty!");
            return true;
        }
        return false;
    }

    private void addSlot() {
        try {
            if (isEmpty(txtDoctorId, "Doctor ID") || isEmpty(txtStartTime, "Start Time") ||
                isEmpty(txtEndTime, "End Time") || isEmpty(txtWeekStart, "Week Start") ||
                isEmpty(txtWeekEnd, "Week End")) return;

            int doctorId = Integer.parseInt(txtDoctorId.getText().trim());
            LocalDateTime start = LocalDateTime.parse(txtStartTime.getText().trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime end = LocalDateTime.parse(txtEndTime.getText().trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDate weekStart = LocalDate.parse(txtWeekStart.getText().trim());
            LocalDate weekEnd = LocalDate.parse(txtWeekEnd.getText().trim());

            // Validation
            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(this, "End time must be after start time!");
                return;
            }
            if (start.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, "Start time must be in the future!");
                return;
            }

            Slot slot = new Slot();
            slot.setDoctorId(doctorId);
            slot.setStartTime(start);
            slot.setEndTime(end);
            slot.setWeekStart(weekStart);
            slot.setWeekEnd(weekEnd);
            slot.setActive(true);

            int id = slotService.create(slot);
            JOptionPane.showMessageDialog(this, "Slot added with ID: " + id);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void viewSlots() {
        try {
            if (isEmpty(txtDoctorId, "Doctor ID")) return;

            int doctorId = Integer.parseInt(txtDoctorId.getText().trim());
            List<Slot> slots = slotService.findAvailabilityForDoctor(doctorId);

            if (slots.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No upcoming availability found for Doctor ID: " + doctorId);
                return;
            }

            StringBuilder sb = new StringBuilder("Doctor Availability:\n");
            for (Slot s : slots) {
                sb.append("From ").append(s.getStartTime())
                  .append(" to ").append(s.getEndTime())
                  .append(" (Week: ").append(s.getWeekStart())
                  .append(" - ").append(s.getWeekEnd()).append(")\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateSlot() {
        try {
            String slotIdText = JOptionPane.showInputDialog(this, "Enter Slot ID to update:");
            if (slotIdText == null || slotIdText.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Slot ID cannot be empty!");
                return;
            }

            if (isEmpty(txtDoctorId, "Doctor ID") || isEmpty(txtStartTime, "Start Time") ||
                isEmpty(txtEndTime, "End Time") || isEmpty(txtWeekStart, "Week Start") ||
                isEmpty(txtWeekEnd, "Week End")) return;

            int slotId = Integer.parseInt(slotIdText.trim());
            int doctorId = Integer.parseInt(txtDoctorId.getText().trim());
            LocalDateTime start = LocalDateTime.parse(txtStartTime.getText().trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime end = LocalDateTime.parse(txtEndTime.getText().trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDate weekStart = LocalDate.parse(txtWeekStart.getText().trim());
            LocalDate weekEnd = LocalDate.parse(txtWeekEnd.getText().trim());

            // Validation
            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(this, "End time must be after start time!");
                return;
            }
            if (start.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, "Start time must be in the future!");
                return;
            }

            Slot slot = new Slot();
            slot.setSlotId(slotId);
            slot.setDoctorId(doctorId);
            slot.setStartTime(start);
            slot.setEndTime(end);
            slot.setWeekStart(weekStart);
            slot.setWeekEnd(weekEnd);
            slot.setActive(true);

            boolean success = slotService.update(slot);
            JOptionPane.showMessageDialog(this, success ? "Slot updated successfully!" : "Slot update failed!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DoctorSlotsFrame::new);
    }
}