package com.yourhotel.booking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomRequest {

    @NotBlank(message = "Room number is required")
    @Size(max = 20)
    private String roomNumber;

    @NotBlank(message = "Room type is required")
    private String roomType;

    @NotNull(message = "Floor is required")
    @Min(0)
    private Integer floor;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @NotNull(message = "Capacity is required")
    @Min(1)
    @Max(10)
    private Integer capacity;

    private String description;
    private String imageUrl;
    private Boolean hasWifi = true;
    private Boolean hasAc = true;
    private Boolean hasBreakfast = false;
    private Boolean hasBalcony = false;
    private Integer sizeSqm;
}
