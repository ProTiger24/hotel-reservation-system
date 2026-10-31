package com.yourhotel.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "travel_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelPackage extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "destination_id")
    private Long destinationId;

    @Column(name = "includes_room", nullable = false)
    @Builder.Default
    private Boolean includesRoom = true;

    @Column(name = "includes_vehicle", nullable = false)
    @Builder.Default
    private Boolean includesVehicle = true;

    @Column(name = "room_type_id")
    private Long roomTypeId;

    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "duration_days", nullable = false)
    @Builder.Default
    private Integer durationDays = 1;

    @Column(name = "duration_nights", nullable = false)
    @Builder.Default
    private Integer durationNights = 0;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "discount_percent", nullable = false)
    @Builder.Default
    private Integer discountPercent = 0;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "final_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
