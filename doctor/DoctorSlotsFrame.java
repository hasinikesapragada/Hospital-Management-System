package project1.doctor;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DoctorSlotsFrame extends JFrame {
    private JTextField txtDoctorId, txtStartTime, txtEndTime, txtWeekStart, txtWeekEnd;
    private JCheckBox chkAutoApprovable;
    private JButton btnAddSlot, btnViewSlots, btnUpdateSlot;

    private SlotService slotService = new SlotService();

    public DoctorSlotsFrame() {
        setTitle("Doctor Weekly Slots");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Form panel (center)
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Doctor ID:"));
        txtDoctorId = new JTextField();
        formPanel.add(txtDoctorId);

        formPanel.add(new JLabel("Start Time (yyyy-MM-dd HH:mm):"));
        txtStartTime = new JTextField();
        formPanel.add(txtStartTime);

        formPanel.add(new JLabel("End Time (yyyy-MM-dd HH:mm):"));
        txtEndTime = new JTextField();
        formPanel.add(txtEndTime);

        formPanel.add(new JLabel("Week Start (yyyy-MM-dd):"));
        txtWeekStart = new JTextField();
        formPanel.add(txtWeekStart);

        formPanel.add(new JLabel("Week End (yyyy-MM-dd):"));
        txtWeekEnd = new JTextField();
        formPanel.add(txtWeekEnd);

        chkAutoApprovable = new JCheckBox("Auto-approvable slot");
        formPanel.add(chkAutoApprovable);
        formPanel.add(new JLabel()); // filler

        add(formPanel, BorderLayout.CENTER);

        // Button panel (south)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnAddSlot = new JButton("Add Slot");
        btnViewSlots = new JButton("View Slots");
        btnUpdateSlot = new JButton("Update Slot");

        buttonPanel.add(btnAddSlot);
        buttonPanel.add(btnViewSlots);
        buttonPanel.add(btnUpdateSlot);

        add(buttonPanel, BorderLayout.SOUTH);

        // Actions
        btnAddSlot.addActionListener(e -> addSlot());
        btnViewSlots.addActionListener(e -> viewSlots());
        btnUpdateSlot.addActionListener(e -> updateSlot());

        setVisible(true);
    }

    // ✅ Helper: validate text field
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
                isEmpty(txtWeekEnd, "Week End")) {
                return;
            }

            int doctorId = Integer.parseInt(txtDoctorId.getText().trim());
            LocalDateTime start = LocalDateTime.parse(txtStartTime.getText().trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime end = LocalDateTime.parse(txtEndTime.getText().trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDate weekStart = LocalDate.parse(txtWeekStart.getText().trim());
            LocalDate weekEnd = LocalDate.parse(txtWeekEnd.getText().trim());
            boolean autoApprovable = chkAutoApprovable.isSelected();

            Slot slot = new Slot();
            slot.setDoctorId(doctorId);
            slot.setStartTime(start);
            slot.setEndTime(end);
            slot.setWeekStart(weekStart);
            slot.setWeekEnd(weekEnd);
            slot.setAutoApprovable(autoApprovable);
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
            List<Slot> slots = slotService.findByDoctor(doctorId);

            if (slots.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No slots found for Doctor ID: " + doctorId);
                return;
            }

            StringBuilder sb = new StringBuilder("Slots:\n");
            for (Slot s : slots) {
                sb.append(s).append("\n");
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
                isEmpty(txtWeekEnd, "Week End")) {
                return;
            }

            int slotId = Integer.parseInt(slotIdText.trim());

            // ✅ Restrict updates if slot has appointments
            if (slotService.hasAppointments(slotId)) {
                JOptionPane.showMessageDialog(this,
                        "This slot already has booked appointments. Updates are restricted!");
                return;
            }

            int doctorId = Integer.parseInt(txtDoctorId.getText().trim());
            LocalDateTime start = LocalDateTime.parse(txtStartTime.getText().trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime end = LocalDateTime.parse(txtEndTime.getText().trim(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDate weekStart = LocalDate.parse(txtWeekStart.getText().trim());
            LocalDate weekEnd = LocalDate.parse(txtWeekEnd.getText().trim());
            boolean autoApprovable = chkAutoApprovable.isSelected();

            Slot slot = new Slot();
            slot.setSlotId(slotId);
            slot.setDoctorId(doctorId);
            slot.setStartTime(start);
            slot.setEndTime(end);
            slot.setWeekStart(weekStart);
            slot.setWeekEnd(weekEnd);
            slot.setAutoApprovable(autoApprovable);
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