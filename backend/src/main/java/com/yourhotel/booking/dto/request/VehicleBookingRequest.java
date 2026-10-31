package com.yourhotel.booking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class VehicleBookingRequest {

    private Long vehicleId;
    private Long packageId;
    
    // 🚀 নতুন যোগ করা হয়েছে
    private Long driverId;

    @NotBlank(message = "Booking type is required")
    private String bookingType;

    private String destination;
    private String pickupLocation;
    private String dropoffLocation;

    @NotNull(message = "Pickup date is required")
    @FutureOrPresent(message = "Pickup date must be today or future")
    private LocalDate pickupDate;

    @NotNull(message = "Return date is required")
    private LocalDate returnDate;

    @NotNull(message = "Days is required")
    @Min(1)
    private Integer days;

    private String promoCode;
    private String specialRequest;
}
