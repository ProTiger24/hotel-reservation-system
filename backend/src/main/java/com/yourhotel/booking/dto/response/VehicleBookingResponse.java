package com.yourhotel.booking.dto.response;

import com.yourhotel.booking.entity.VehicleBooking;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleBookingResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long vehicleId;
    private String vehicleNumber;
    private Long driverId;
    private String driverName;
    private Long packageId;
    private String packageName;
    private String bookingType;
    private String destination;
    private String pickupLocation;
    private String dropoffLocation;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private Integer days;
    private BigDecimal basePrice;
    private BigDecimal discountAmount;
    private BigDecimal totalPrice;
    private String promoCode;
    private String status;
    private String specialRequest;
    private LocalDateTime createdAt;

    public static VehicleBookingResponse fromEntity(VehicleBooking vb) {
        return VehicleBookingResponse.builder()
                .id(vb.getId())
                .userId(vb.getUserId())
                .vehicleId(vb.getVehicleId())
                .driverId(vb.getDriverId())
                .packageId(vb.getPackageId())
                .bookingType(vb.getBookingType())
                .destination(vb.getDestination())
                .pickupLocation(vb.getPickupLocation())
                .dropoffLocation(vb.getDropoffLocation())
                .pickupDate(vb.getPickupDate())
                .returnDate(vb.getReturnDate())
                .days(vb.getDays())
                .basePrice(vb.getBasePrice())
                .discountAmount(vb.getDiscountAmount())
                .totalPrice(vb.getTotalPrice())
                .promoCode(vb.getPromoCode())
                .status(vb.getStatus().name())
                .specialRequest(vb.getSpecialRequest())
                .createdAt(vb.getCreatedAt())
                .build();
    }
}
