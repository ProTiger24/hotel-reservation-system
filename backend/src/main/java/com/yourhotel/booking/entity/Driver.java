package com.yourhotel.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "license_number", unique = true, length = 50)
    private String licenseNumber;

    @Column(name = "experience_years")
    @Builder.Default
    private Integer experienceYears = 0;

    @Column(name = "assigned_vehicle_id")
    private Long assignedVehicleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DriverStatus status = DriverStatus.AVAILABLE;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    public enum DriverStatus {
        AVAILABLE, ON_TRIP, OFF_DUTY
    }
}
