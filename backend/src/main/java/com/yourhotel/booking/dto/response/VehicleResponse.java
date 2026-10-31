package com.yourhotel.booking.dto.response;

import com.yourhotel.booking.entity.Vehicle;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {
    private Long id;
    private String vehicleNumber;
    private String vehicleType;
    private String brand;
    private String model;
    private Integer seatCapacity;
    private String acType;
    private BigDecimal perHourPrice;
    private BigDecimal perDayPrice;
    private String imageUrl;
    private String status;
    private String description;

    public static VehicleResponse fromEntity(Vehicle v) {
        return VehicleResponse.builder()
                .id(v.getId())
                .vehicleNumber(v.getVehicleNumber())
                .vehicleType(v.getVehicleType())
                .brand(v.getBrand())
                .model(v.getModel())
                .seatCapacity(v.getSeatCapacity())
                .acType(v.getAcType())
                .perHourPrice(v.getPerHourPrice())
                .perDayPrice(v.getPerDayPrice())
                .imageUrl(v.getImageUrl())
                .status(v.getStatus().name())
                .description(v.getDescription())
                .build();
    }
}
