package com.yourhotel.booking.dto.response;

import com.yourhotel.booking.entity.Destination;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DestinationResponse {
    private Long id;
    private String name;
    private String description;
    private Integer distanceKm;
    private Integer estimatedHours;
    private BigDecimal baseFare;
    private String imageUrl;
    private Boolean isActive;

    public static DestinationResponse fromEntity(Destination d) {
        return DestinationResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .distanceKm(d.getDistanceKm())
                .estimatedHours(d.getEstimatedHours())
                .baseFare(d.getBaseFare())
                .imageUrl(d.getImageUrl())
                .isActive(d.getIsActive())
                .build();
    }
}
