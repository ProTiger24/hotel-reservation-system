package com.yourhotel.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "vehicle_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleBooking extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "driver_id")
    private Long driverId;

    @Column(name = "package_id")
    private Long packageId;

    @Column(name = "booking_type", nullable = false, length = 30)
    @Builder.Default
    private String bookingType = "VEHICLE_ONLY";  // VEHICLE_ONLY, PACKAGE, AIRPORT_PICKUP

    @Column(length = 200)
    private String destination;

    @Column(name = "pickup_location", length = 200)
    private String pickupLocation;

    @Column(name = "dropoff_location", length = 200)
    private String dropoffLocation;

    @Column(name = "pickup_date", nullable = false)
    private LocalDate pickupDate;

    @Column(name = "return_date", nullable = false)
    private LocalDate returnDate;

    @Column(nullable = false)
    private Integer days;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "promo_code", length = 50)
    private String promoCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "hotel_booking_id")
    private Long hotelBookingId;

    @Column(name = "special_request", columnDefinition = "TEXT")
    private String specialRequest;

    public enum BookingStatus {
        PENDING, CONFIRMED, ON_TRIP, COMPLETED, CANCELLED
    }
}
