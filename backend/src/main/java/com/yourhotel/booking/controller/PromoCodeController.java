package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.dto.response.PromoCodeResponse;
import com.yourhotel.booking.entity.PromoCode;
import com.yourhotel.booking.exception.BadRequestException;
import com.yourhotel.booking.repository.PromoCodeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/promo-codes")
public class PromoCodeController {

    private final PromoCodeRepository promoCodeRepository;

    public PromoCodeController(PromoCodeRepository promoCodeRepository) {
        this.promoCodeRepository = promoCodeRepository;
    }

    // Get all active promos (for popup)
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<PromoCodeResponse>>> getActive() {
        List<PromoCodeResponse> list = promoCodeRepository.findAll().stream()
                .filter(PromoCode::getIsActive)
                .filter(p -> p.getValidUntil() == null || !p.getValidUntil().isBefore(LocalDate.now()))
                .filter(p -> p.getUsageLimit() == null || p.getUsedCount() < p.getUsageLimit())
                .map(PromoCodeResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    // Validate promo code for a specific amount
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validate(@RequestBody Map<String, Object> body) {
        String code = ((String) body.get("code")).trim().toUpperCase();
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        String appliesTo = body.getOrDefault("appliesTo", "ALL").toString();

        PromoCode p = promoCodeRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new BadRequestException("Invalid promo code"));

        if (p.getValidFrom() != null && p.getValidFrom().isAfter(LocalDate.now())) {
            throw new BadRequestException("Promo code is not yet valid");
        }
        if (p.getValidUntil() != null && p.getValidUntil().isBefore(LocalDate.now())) {
            throw new BadRequestException("Promo code has expired");
        }
        if (p.getUsageLimit() != null && p.getUsedCount() >= p.getUsageLimit()) {
            throw new BadRequestException("Promo code usage limit reached");
        }
        if (p.getMinOrderAmount() != null && amount.compareTo(p.getMinOrderAmount()) < 0) {
            throw new BadRequestException("Minimum order amount is ৳" + p.getMinOrderAmount());
        }
        if (p.getAppliesTo() != PromoCode.AppliesTo.ALL
            && !p.getAppliesTo().name().equalsIgnoreCase(appliesTo)) {
            throw new BadRequestException("Promo code not applicable for this booking");
        }

        BigDecimal discountAmount;
        if (p.getDiscountType() == PromoCode.DiscountType.PERCENTAGE) {
            discountAmount = amount.multiply(p.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (p.getMaxDiscount() != null && discountAmount.compareTo(p.getMaxDiscount()) > 0) {
                discountAmount = p.getMaxDiscount();
            }
        } else {
            discountAmount = p.getDiscountValue();
        }
        if (discountAmount.compareTo(amount) > 0) discountAmount = amount;

        BigDecimal finalAmount = amount.subtract(discountAmount);

        Map<String, Object> result = new HashMap<>();
        result.put("code", p.getCode());
        result.put("description", p.getDescription());
        result.put("discountType", p.getDiscountType().name());
        result.put("discountValue", p.getDiscountValue());
        result.put("originalAmount", amount);
        result.put("discountAmount", discountAmount);
        result.put("finalAmount", finalAmount);

        return ResponseEntity.ok(ApiResponse.success("Promo code applied", result));
    }
}
