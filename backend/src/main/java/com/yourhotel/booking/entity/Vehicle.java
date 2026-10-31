package com.yourhotel.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle extends BaseEntity {

    @Column(name = "vehicle_number", nullable = false, unique = true, length = 20)
    private String vehicleNumber;

    @Column(name = "vehicle_type", nullable = false, length = 50)
    private String vehicleType;

    @Column(length = 50)
    private String brand;

    @Column(length = 50)
    private String model;

    @Column(name = "seat_capacity", nullable = false)
    private Integer seatCapacity;

    @Column(name = "ac_type", length = 20)
    @Builder.Default
    private String acType = "AC";

    @Column(name = "per_hour_price", precision = 10, scale = 2)
    private BigDecimal perHourPrice;

    @Column(name = "per_day_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal perDayPrice;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @Column(columnDefinition = "TEXT")
    private String description;

    public enum VehicleStatus {
        AVAILABLE, BOOKED, MAINTENANCE
    }
}
