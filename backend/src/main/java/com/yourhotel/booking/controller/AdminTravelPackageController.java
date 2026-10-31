package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.request.TravelPackageRequest;
import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.dto.response.TravelPackageResponse;
import com.yourhotel.booking.entity.*;
import com.yourhotel.booking.exception.BadRequestException;
import com.yourhotel.booking.exception.ResourceNotFoundException;
import com.yourhotel.booking.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/packages")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTravelPackageController {

    private final TravelPackageRepository packageRepository;
    private final DestinationRepository destinationRepository;
    private final VehicleRepository vehicleRepository;
    private final RoomTypeRepository roomTypeRepository;

    public AdminTravelPackageController(TravelPackageRepository packageRepository,
                                         DestinationRepository destinationRepository,
                                         VehicleRepository vehicleRepository,
                                         RoomTypeRepository roomTypeRepository) {
        this.packageRepository = packageRepository;
        this.destinationRepository = destinationRepository;
        this.vehicleRepository = vehicleRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TravelPackageResponse>>> getAll() {
        List<TravelPackageResponse> list = packageRepository.findAll().stream()
                .map(this::enrich)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TravelPackageResponse>> getById(@PathVariable Long id) {
        TravelPackage p = packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found"));
        return ResponseEntity.ok(ApiResponse.success(enrich(p)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TravelPackageResponse>> create(@Valid @RequestBody TravelPackageRequest req) {
        TravelPackage p = buildFromRequest(new TravelPackage(), req);
        TravelPackage saved = packageRepository.save(p);
        return ResponseEntity.ok(ApiResponse.success("Package created", enrich(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TravelPackageResponse>> update(@PathVariable Long id,
                                                                       @Valid @RequestBody TravelPackageRequest req) {
        TravelPackage p = packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found"));
        buildFromRequest(p, req);
        TravelPackage saved = packageRepository.save(p);
        return ResponseEntity.ok(ApiResponse.success("Package updated", enrich(saved)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        TravelPackage p = packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found"));
        packageRepository.delete(p);
        return ResponseEntity.ok(ApiResponse.success("Package deleted", null));
    }

    private TravelPackage buildFromRequest(TravelPackage p, TravelPackageRequest req) {
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setDestinationId(req.getDestinationId());
        p.setIncludesRoom(req.getIncludesRoom() != null ? req.getIncludesRoom() : true);
        p.setIncludesVehicle(req.getIncludesVehicle() != null ? req.getIncludesVehicle() : true);
        p.setRoomTypeId(req.getRoomTypeId());
        p.setVehicleId(req.getVehicleId());
        p.setDurationDays(req.getDurationDays());
        p.setDurationNights(req.getDurationNights() != null ? req.getDurationNights() : 0);
        p.setBasePrice(req.getBasePrice());

        // Calculate discount
        Integer pct = req.getDiscountPercent() != null ? req.getDiscountPercent() : 0;
        BigDecimal extraAmt = req.getDiscountAmount() != null ? req.getDiscountAmount() : BigDecimal.ZERO;

        BigDecimal pctAmount = req.getBasePrice()
                .multiply(BigDecimal.valueOf(pct))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal totalDiscount = pctAmount.add(extraAmt);
        BigDecimal finalPrice = req.getBasePrice().subtract(totalDiscount);
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) finalPrice = BigDecimal.ZERO;

        p.setDiscountPercent(pct);
        p.setDiscountAmount(totalDiscount);
        p.setFinalPrice(finalPrice);
        p.setImageUrl(req.getImageUrl());
        p.setIsActive(req.getIsActive() != null ? req.getIsActive() : true);

        return p;
    }

    private TravelPackageResponse enrich(TravelPackage p) {
        TravelPackageResponse r = TravelPackageResponse.fromEntity(p);
        if (p.getDestinationId() != null) {
            destinationRepository.findById(p.getDestinationId())
                    .ifPresent(d -> r.setDestinationName(d.getName()));
        }
        if (p.getVehicleId() != null) {
            vehicleRepository.findById(p.getVehicleId())
                    .ifPresent(v -> r.setVehicleNumber(v.getVehicleNumber()));
        }
        if (p.getRoomTypeId() != null) {
            roomTypeRepository.findById(p.getRoomTypeId())
                    .ifPresent(rt -> r.setRoomTypeName(rt.getName()));
        }
        return r;
    }
}
