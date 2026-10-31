package com.yourhotel.booking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TravelPackageRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private Long destinationId;

    private Boolean includesRoom = true;
    private Boolean includesVehicle = true;
    private Long roomTypeId;
    private Long vehicleId;

    @NotNull(message = "Duration days is required")
    @Min(1)
    private Integer durationDays;

    @Min(0)
    private Integer durationNights;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal basePrice;

    @Min(0) @Max(100)
    private Integer discountPercent = 0;

    private BigDecimal discountAmount = BigDecimal.ZERO;

    private String imageUrl;

    private Boolean isActive = true;
}
