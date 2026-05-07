package project1.doctor;

import project1.doctor.Slot;
import project1.doctor.SlotService;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class Slot {
    private int slotId;
    private int doctorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private boolean active;

    // Optional: doctors can add a note about the slot (e.g., "Morning OPD")
    private String availabilityNote;

    // Getters and Setters
    public int getSlotId() { return slotId; }
    public void setSlotId(int slotId) { this.slotId = slotId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public LocalDate getWeekStart() { return weekStart; }
    public void setWeekStart(LocalDate weekStart) { this.weekStart = weekStart; }

    public LocalDate getWeekEnd() { return weekEnd; }
    public void setWeekEnd(LocalDate weekEnd) { this.weekEnd = weekEnd; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getAvailabilityNote() { return availabilityNote; }
    public void setAvailabilityNote(String availabilityNote) { this.availabilityNote = availabilityNote; }

    @Override
    public String toString() {
        return "Doctor " + doctorId +
               " available from " + startTime +
               " to " + endTime +
               " (Week: " + weekStart + " - " + weekEnd + ")" +
               (availabilityNote != null ? " - " + availabilityNote : "");
    }
}