package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.request.PromoCodeRequest;
import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.dto.response.PromoCodeResponse;
import com.yourhotel.booking.entity.PromoCode;
import com.yourhotel.booking.exception.BadRequestException;
import com.yourhotel.booking.exception.DuplicateResourceException;
import com.yourhotel.booking.exception.ResourceNotFoundException;
import com.yourhotel.booking.repository.PromoCodeRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/promo-codes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPromoCodeController {

    private final PromoCodeRepository promoCodeRepository;

    public AdminPromoCodeController(PromoCodeRepository promoCodeRepository) {
        this.promoCodeRepository = promoCodeRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PromoCodeResponse>>> getAll() {
        // ✅ Sort by ID DESC (latest first) so rows don't jump around on reload
        List<PromoCodeResponse> list = promoCodeRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(PromoCodeResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromoCodeResponse>> getById(@PathVariable Long id) {
        PromoCode p = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found"));
        return ResponseEntity.ok(ApiResponse.success(PromoCodeResponse.fromEntity(p)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PromoCodeResponse>> create(@Valid @RequestBody PromoCodeRequest req) {
        String codeUpper = req.getCode().trim().toUpperCase();
        if (promoCodeRepository.existsByCode(codeUpper)) {
            throw new DuplicateResourceException("Promo code already exists");
        }

        PromoCode p = buildFromRequest(new PromoCode(), req, codeUpper);
        PromoCode saved = promoCodeRepository.save(p);
        return ResponseEntity.ok(ApiResponse.success("Promo code created", PromoCodeResponse.fromEntity(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PromoCodeResponse>> update(@PathVariable Long id,
                                                                   @Valid @RequestBody PromoCodeRequest req) {
        PromoCode p = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found"));

        String codeUpper = req.getCode().trim().toUpperCase();
        if (!p.getCode().equals(codeUpper) && promoCodeRepository.existsByCode(codeUpper)) {
            throw new DuplicateResourceException("Promo code already exists");
        }

        buildFromRequest(p, req, codeUpper);
        PromoCode saved = promoCodeRepository.save(p);
        return ResponseEntity.ok(ApiResponse.success("Promo code updated", PromoCodeResponse.fromEntity(saved)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PromoCodeResponse>> toggleStatus(@PathVariable Long id) {
        PromoCode p = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found"));
        p.setIsActive(!p.getIsActive());
        PromoCode saved = promoCodeRepository.save(p);
        return ResponseEntity.ok(ApiResponse.success(
                saved.getIsActive() ? "Promo activated" : "Promo deactivated",
                PromoCodeResponse.fromEntity(saved)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        PromoCode p = promoCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promo code not found"));
        promoCodeRepository.delete(p);
        return ResponseEntity.ok(ApiResponse.success("Promo code deleted", null));
    }

    private PromoCode buildFromRequest(PromoCode p, PromoCodeRequest req, String codeUpper) {
        try {
            p.setCode(codeUpper);
            p.setDescription(req.getDescription());
            p.setDiscountType(PromoCode.DiscountType.valueOf(req.getDiscountType().toUpperCase()));
            p.setDiscountValue(req.getDiscountValue());
            p.setMaxDiscount(req.getMaxDiscount());
            p.setMinOrderAmount(req.getMinOrderAmount() != null ? req.getMinOrderAmount() : java.math.BigDecimal.ZERO);
            p.setAppliesTo(PromoCode.AppliesTo.valueOf(req.getAppliesTo().toUpperCase()));
            p.setValidFrom(req.getValidFrom());
            p.setValidUntil(req.getValidUntil());
            p.setUsageLimit(req.getUsageLimit());
            p.setIsActive(req.getIsActive() != null ? req.getIsActive() : true);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid discount type or applies-to value");
        }
        return p;
    }
}
