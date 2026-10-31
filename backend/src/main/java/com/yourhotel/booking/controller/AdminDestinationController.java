package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.request.DestinationRequest;
import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.dto.response.DestinationResponse;
import com.yourhotel.booking.entity.Destination;
import com.yourhotel.booking.exception.ResourceNotFoundException;
import com.yourhotel.booking.repository.DestinationRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/destinations")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDestinationController {

    private final DestinationRepository destinationRepository;

    public AdminDestinationController(DestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DestinationResponse>>> getAll() {
        List<DestinationResponse> list = destinationRepository.findAll().stream()
                .map(DestinationResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DestinationResponse>> getById(@PathVariable Long id) {
        Destination d = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found"));
        return ResponseEntity.ok(ApiResponse.success(DestinationResponse.fromEntity(d)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DestinationResponse>> create(@Valid @RequestBody DestinationRequest req) {
        Destination d = Destination.builder()
                .name(req.getName())
                .description(req.getDescription())
                .distanceKm(req.getDistanceKm())
                .estimatedHours(req.getEstimatedHours())
                .baseFare(req.getBaseFare())
                .imageUrl(req.getImageUrl())
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .build();

        Destination saved = destinationRepository.save(d);
        return ResponseEntity.ok(ApiResponse.success("Destination added", DestinationResponse.fromEntity(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DestinationResponse>> update(@PathVariable Long id,
                                                                     @Valid @RequestBody DestinationRequest req) {
        Destination d = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found"));

        d.setName(req.getName());
        d.setDescription(req.getDescription());
        d.setDistanceKm(req.getDistanceKm());
        d.setEstimatedHours(req.getEstimatedHours());
        d.setBaseFare(req.getBaseFare());
        d.setImageUrl(req.getImageUrl());
        if (req.getIsActive() != null) d.setIsActive(req.getIsActive());

        Destination saved = destinationRepository.save(d);
        return ResponseEntity.ok(ApiResponse.success("Destination updated", DestinationResponse.fromEntity(saved)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        Destination d = destinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found"));
        destinationRepository.delete(d);
        return ResponseEntity.ok(ApiResponse.success("Destination deleted", null));
    }
}
