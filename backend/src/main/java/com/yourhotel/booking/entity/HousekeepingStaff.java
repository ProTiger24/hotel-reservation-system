package com.yourhotel.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "housekeeping_staff")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HousekeepingStaff extends BaseEntity {
    private String name;
    private String phone;
    private String shift; // MORNING, EVENING, NIGHT
    @Enumerated(EnumType.STRING)
    private StaffStatus status; // AVAILABLE, ON_BREAK, OFF_DUTY
    
    @OneToMany(mappedBy = "assignedStaff")
    private List<HousekeepingTask> tasks;

    public enum StaffStatus { AVAILABLE, ON_BREAK, OFF_DUTY }
}
