package com.yourhotel.booking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehicleRequest {

    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;

    @NotBlank(message = "Vehicle type is required")
    private String vehicleType;

    private String brand;
    private String model;

    @NotNull(message = "Seat capacity is required")
    @Min(1)
    private Integer seatCapacity;

    private String acType;

    private BigDecimal perHourPrice;

    @NotNull(message = "Per day price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal perDayPrice;

    private String imageUrl;
    private String description;
}
