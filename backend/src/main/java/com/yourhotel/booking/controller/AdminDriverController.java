package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.request.DriverRequest;
import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.dto.response.DriverResponse;
import com.yourhotel.booking.entity.Driver;
import com.yourhotel.booking.entity.Vehicle;
import com.yourhotel.booking.exception.BadRequestException;
import com.yourhotel.booking.exception.DuplicateResourceException;
import com.yourhotel.booking.exception.ResourceNotFoundException;
import com.yourhotel.booking.repository.DriverRepository;
import com.yourhotel.booking.repository.VehicleRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/drivers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDriverController {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    public AdminDriverController(DriverRepository driverRepository, VehicleRepository vehicleRepository) {
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DriverResponse>>> getAll() {
        List<DriverResponse> list = driverRepository.findAll().stream()
                .map(this::enrich)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverResponse>> getById(@PathVariable Long id) {
        Driver d = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        return ResponseEntity.ok(ApiResponse.success(enrich(d)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DriverResponse>> create(@Valid @RequestBody DriverRequest req) {
        if (req.getLicenseNumber() != null && !req.getLicenseNumber().isEmpty()
            && driverRepository.existsByLicenseNumber(req.getLicenseNumber())) {
            throw new DuplicateResourceException("License number already exists");
        }

        Driver d = Driver.builder()
                .name(req.getName())
                .phone(req.getPhone())
                .licenseNumber(req.getLicenseNumber())
                .experienceYears(req.getExperienceYears() != null ? req.getExperienceYears() : 0)
                .assignedVehicleId(req.getAssignedVehicleId())
                .imageUrl(req.getImageUrl())
                .status(Driver.DriverStatus.AVAILABLE)
                .build();

        Driver saved = driverRepository.save(d);
        return ResponseEntity.ok(ApiResponse.success("Driver added", enrich(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DriverResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody DriverRequest req) {
        Driver d = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        d.setName(req.getName());
        d.setPhone(req.getPhone());
        d.setLicenseNumber(req.getLicenseNumber());
        d.setExperienceYears(req.getExperienceYears());
        d.setAssignedVehicleId(req.getAssignedVehicleId());
        d.setImageUrl(req.getImageUrl());

        Driver saved = driverRepository.save(d);
        return ResponseEntity.ok(ApiResponse.success("Driver updated", enrich(saved)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<DriverResponse>> updateStatus(@PathVariable Long id,
                                                                      @RequestParam String status) {
        Driver d = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

        try {
            d.setStatus(Driver.DriverStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status");
        }

        Driver saved = driverRepository.save(d);
        return ResponseEntity.ok(ApiResponse.success("Status updated", enrich(saved)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        Driver d = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        driverRepository.delete(d);
        return ResponseEntity.ok(ApiResponse.success("Driver deleted", null));
    }

    private DriverResponse enrich(Driver d) {
        DriverResponse r = DriverResponse.fromEntity(d);
        if (d.getAssignedVehicleId() != null) {
            vehicleRepository.findById(d.getAssignedVehicleId()).ifPresent(v ->
                    r.setAssignedVehicleNumber(v.getVehicleNumber()));
        }
        return r;
    }
}
