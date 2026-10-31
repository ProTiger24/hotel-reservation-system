package com.yourhotel.booking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DestinationRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @Min(0)
    private Integer distanceKm;

    @Min(0)
    private Integer estimatedHours;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal baseFare;

    private String imageUrl;

    private Boolean isActive = true;
}
