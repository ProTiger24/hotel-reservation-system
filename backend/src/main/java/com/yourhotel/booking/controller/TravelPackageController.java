package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.dto.response.TravelPackageResponse;
import com.yourhotel.booking.entity.TravelPackage;
import com.yourhotel.booking.exception.ResourceNotFoundException;
import com.yourhotel.booking.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/travel-packages")
public class TravelPackageController {

    private final TravelPackageRepository packageRepository;
    private final DestinationRepository destinationRepository;
    private final VehicleRepository vehicleRepository;
    private final RoomTypeRepository roomTypeRepository;

    public TravelPackageController(TravelPackageRepository packageRepository,
                                    DestinationRepository destinationRepository,
                                    VehicleRepository vehicleRepository,
                                    RoomTypeRepository roomTypeRepository) {
        this.packageRepository = packageRepository;
        this.destinationRepository = destinationRepository;
        this.vehicleRepository = vehicleRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TravelPackageResponse>>> getActive() {
        List<TravelPackageResponse> list = packageRepository.findByIsActiveTrue().stream()
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

    @GetMapping("/destination/{destinationId}")
    public ResponseEntity<ApiResponse<List<TravelPackageResponse>>> getByDestination(@PathVariable Long destinationId) {
        List<TravelPackageResponse> list = packageRepository.findByDestinationId(destinationId).stream()
                .filter(TravelPackage::getIsActive)
                .map(this::enrich)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
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
