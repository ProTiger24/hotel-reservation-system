package com.yourhotel.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "promo_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCode extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiscountType discountType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @Column(precision = 10, scale = 2)
    private BigDecimal maxDiscount;

    @Column(precision = 10, scale = 2)
    private BigDecimal minOrderAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppliesTo appliesTo;

    private LocalDate validFrom;
    private LocalDate validUntil;

    private Integer usageLimit;

    @Column(nullable = false)
    @Builder.Default
    private Integer usedCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    public enum DiscountType { PERCENTAGE, FIXED }
    public enum AppliesTo { ALL, ROOM, TRAVEL }
}
