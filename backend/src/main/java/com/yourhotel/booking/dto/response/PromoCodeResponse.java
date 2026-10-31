package com.yourhotel.booking.dto.response;

import com.yourhotel.booking.entity.PromoCode;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeResponse {
    private Long id;
    private String code;
    private String description;
    private String discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscount;
    private BigDecimal minOrderAmount;
    private String appliesTo;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private Integer usageLimit;
    private Integer usedCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Boolean isExpired;

    public static PromoCodeResponse fromEntity(PromoCode p) {
        boolean expired = p.getValidUntil() != null && p.getValidUntil().isBefore(LocalDate.now());
        return PromoCodeResponse.builder()
                .id(p.getId())
                .code(p.getCode())
                .description(p.getDescription())
                .discountType(p.getDiscountType().name())
                .discountValue(p.getDiscountValue())
                .maxDiscount(p.getMaxDiscount())
                .minOrderAmount(p.getMinOrderAmount())
                .appliesTo(p.getAppliesTo().name())
                .validFrom(p.getValidFrom())
                .validUntil(p.getValidUntil())
                .usageLimit(p.getUsageLimit())
                .usedCount(p.getUsedCount())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .isExpired(expired)
                .build();
    }
}
