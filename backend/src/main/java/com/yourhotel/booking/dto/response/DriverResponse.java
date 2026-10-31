package com.yourhotel.booking.dto.response;

import com.yourhotel.booking.entity.Driver;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {
    private Long id;
    private String name;
    private String phone;
    private String licenseNumber;
    private Integer experienceYears;
    private Long assignedVehicleId;
    private String assignedVehicleNumber;
    private String status;
    private String imageUrl;

    public static DriverResponse fromEntity(Driver d) {
        return DriverResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .phone(d.getPhone())
                .licenseNumber(d.getLicenseNumber())
                .experienceYears(d.getExperienceYears())
                .assignedVehicleId(d.getAssignedVehicleId())
                .status(d.getStatus().name())
                .imageUrl(d.getImageUrl())
                .build();
    }
}
