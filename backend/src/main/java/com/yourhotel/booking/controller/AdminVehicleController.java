package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.request.VehicleRequest;
import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.dto.response.VehicleResponse;
import com.yourhotel.booking.entity.Vehicle;
import com.yourhotel.booking.exception.BadRequestException;
import com.yourhotel.booking.exception.DuplicateResourceException;
import com.yourhotel.booking.exception.ResourceNotFoundException;
import com.yourhotel.booking.repository.VehicleRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/vehicles")
@PreAuthorize("hasRole('ADMIN')")
public class AdminVehicleController {

    private final VehicleRepository vehicleRepository;

    public AdminVehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getAll() {
        List<VehicleResponse> list = vehicleRepository.findAll().stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> getById(@PathVariable Long id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        return ResponseEntity.ok(ApiResponse.success(VehicleResponse.fromEntity(v)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponse>> create(@Valid @RequestBody VehicleRequest req) {
        if (vehicleRepository.existsByVehicleNumber(req.getVehicleNumber())) {
            throw new DuplicateResourceException("Vehicle number already exists");
        }

        Vehicle v = Vehicle.builder()
                .vehicleNumber(req.getVehicleNumber())
                .vehicleType(req.getVehicleType())
                .brand(req.getBrand())
                .model(req.getModel())
                .seatCapacity(req.getSeatCapacity())
                .acType(req.getAcType() != null ? req.getAcType() : "AC")
                .perHourPrice(req.getPerHourPrice())
                .perDayPrice(req.getPerDayPrice())
                .imageUrl(req.getImageUrl())
                .description(req.getDescription())
                .status(Vehicle.VehicleStatus.AVAILABLE)
                .build();

        Vehicle saved = vehicleRepository.save(v);
        return ResponseEntity.ok(ApiResponse.success("Vehicle added", VehicleResponse.fromEntity(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody VehicleRequest req) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        if (!v.getVehicleNumber().equals(req.getVehicleNumber())
            && vehicleRepository.existsByVehicleNumber(req.getVehicleNumber())) {
            throw new DuplicateResourceException("Vehicle number already exists");
        }

        v.setVehicleNumber(req.getVehicleNumber());
        v.setVehicleType(req.getVehicleType());
        v.setBrand(req.getBrand());
        v.setModel(req.getModel());
        v.setSeatCapacity(req.getSeatCapacity());
        v.setAcType(req.getAcType());
        v.setPerHourPrice(req.getPerHourPrice());
        v.setPerDayPrice(req.getPerDayPrice());
        v.setImageUrl(req.getImageUrl());
        v.setDescription(req.getDescription());

        Vehicle saved = vehicleRepository.save(v);
        return ResponseEntity.ok(ApiResponse.success("Vehicle updated", VehicleResponse.fromEntity(saved)));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<VehicleResponse>> updateStatus(@PathVariable Long id,
                                                                       @RequestParam String status) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        try {
            v.setStatus(Vehicle.VehicleStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status");
        }

        Vehicle saved = vehicleRepository.save(v);
        return ResponseEntity.ok(ApiResponse.success("Status updated", VehicleResponse.fromEntity(saved)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        Vehicle v = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        vehicleRepository.delete(v);
        return ResponseEntity.ok(ApiResponse.success("Vehicle deleted", null));
    }
}
