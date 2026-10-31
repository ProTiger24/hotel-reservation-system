package com.yourhotel.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "housekeeping_tasks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HousekeepingTask extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private HousekeepingStaff assignedStaff;

    @Enumerated(EnumType.STRING)
    private TaskType type; // DEPARTURE_CLEAN, STAYOVER_CLEAN, DEEP_CLEAN, INSPECTION

    @Enumerated(EnumType.STRING)
    private TaskStatus status; // PENDING, IN_PROGRESS, COMPLETED, INSPECTED, FAILED

    private String checklistData; // JSON string for checklist items
    private String notes;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public enum TaskType { DEPARTURE_CLEAN, STAYOVER_CLEAN, DEEP_CLEAN, INSPECTION }
    public enum TaskStatus { PENDING, IN_PROGRESS, COMPLETED, INSPECTED, FAILED }
}
