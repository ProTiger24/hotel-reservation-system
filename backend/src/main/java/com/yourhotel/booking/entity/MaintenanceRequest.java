package com.yourhotel.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "maintenance_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MaintenanceRequest extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    private String issueDescription;
    
    @Enumerated(EnumType.STRING)
    private Priority priority; // LOW, MEDIUM, HIGH, URGENT

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // OPEN, IN_PROGRESS, RESOLVED, CANCELLED

    public enum Priority { LOW, MEDIUM, HIGH, URGENT }
    public enum RequestStatus { OPEN, IN_PROGRESS, RESOLVED, CANCELLED }
}
