package com.yourhotel.booking.dto.response;

import com.yourhotel.booking.entity.TravelPackage;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelPackageResponse {
    private Long id;
    private String name;
    private String description;
    private Long destinationId;
    private String destinationName;
    private Boolean includesRoom;
    private Boolean includesVehicle;
    private Long roomTypeId;
    private String roomTypeName;
    private Long vehicleId;
    private String vehicleNumber;
    private Integer durationDays;
    private Integer durationNights;
    private BigDecimal basePrice;
    private Integer discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;
    private String imageUrl;
    private Boolean isActive;

    public static TravelPackageResponse fromEntity(TravelPackage p) {
        return TravelPackageResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .destinationId(p.getDestinationId())
                .includesRoom(p.getIncludesRoom())
                .includesVehicle(p.getIncludesVehicle())
                .roomTypeId(p.getRoomTypeId())
                .vehicleId(p.getVehicleId())
                .durationDays(p.getDurationDays())
                .durationNights(p.getDurationNights())
                .basePrice(p.getBasePrice())
                .discountPercent(p.getDiscountPercent())
                .discountAmount(p.getDiscountAmount())
                .finalPrice(p.getFinalPrice())
                .imageUrl(p.getImageUrl())
                .isActive(p.getIsActive())
                .build();
    }
}
