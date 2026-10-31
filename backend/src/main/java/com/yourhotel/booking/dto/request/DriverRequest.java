package com.yourhotel.booking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DriverRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String licenseNumber;

    @Min(0)
    private Integer experienceYears;

    private Long assignedVehicleId;

    private String imageUrl;
}
