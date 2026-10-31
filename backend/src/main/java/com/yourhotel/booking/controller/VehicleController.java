package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.dto.response.VehicleResponse;
import com.yourhotel.booking.entity.Vehicle;
import com.yourhotel.booking.exception.ResourceNotFoundException;
import com.yourhotel.booking.repository.VehicleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getAvailable() {
        List<VehicleResponse> list = vehicleRepository.findByStatus(Vehicle.VehicleStatus.AVAILABLE)
                .stream()
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
}
