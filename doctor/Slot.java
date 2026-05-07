package project1.doctor;

import java.time.LocalDateTime;
import java.time.LocalDate;

public class Slot {
    private int slotId;
    private int doctorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private boolean autoApprovable;
    private boolean active;

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

    public boolean isAutoApprovable() { return autoApprovable; }
    public void setAutoApprovable(boolean autoApprovable) { this.autoApprovable = autoApprovable; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "Slot{" +
                "slotId=" + slotId +
                ", doctorId=" + doctorId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", weekStart=" + weekStart +
                ", weekEnd=" + weekEnd +
                ", autoApprovable=" + autoApprovable +
                ", active=" + active +
                '}';
    }
}