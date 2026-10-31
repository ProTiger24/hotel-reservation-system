package com.yourhotel.booking.controller;

import com.yourhotel.booking.dto.request.VehicleBookingRequest;
import com.yourhotel.booking.dto.response.ApiResponse;
import com.yourhotel.booking.dto.response.VehicleBookingResponse;
import com.yourhotel.booking.entity.*;
import com.yourhotel.booking.exception.BadRequestException;
import com.yourhotel.booking.exception.ResourceNotFoundException;
import com.yourhotel.booking.repository.*;
import com.yourhotel.booking.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehicle-bookings")
public class VehicleBookingController {

    private final VehicleBookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final TravelPackageRepository packageRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final EmailService emailService; // 🚀 Added

    public VehicleBookingController(VehicleBookingRepository bookingRepository,
                                     UserRepository userRepository,
                                     VehicleRepository vehicleRepository,
                                     DriverRepository driverRepository,
                                     TravelPackageRepository packageRepository,
                                     PromoCodeRepository promoCodeRepository,
                                     EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.packageRepository = packageRepository;
        this.promoCodeRepository = promoCodeRepository;
        this.emailService = emailService;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ApiResponse<VehicleBookingResponse>> create(
            @Valid @RequestBody VehicleBookingRequest req,
            Authentication auth) {

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (req.getReturnDate().isBefore(req.getPickupDate())) {
            throw new BadRequestException("Return date must be after pickup date");
        }

        BigDecimal basePrice;
        TravelPackage pkg = null;
        Vehicle vehicle = null;
        Driver driver = null;

        if (req.getDriverId() != null) {
            driver = driverRepository.findById(req.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
            if (!"AVAILABLE".equalsIgnoreCase(driver.getStatus().toString())) {
                throw new BadRequestException("Selected driver is not available");
            }
        }

        if (req.getPackageId() != null) {
            pkg = packageRepository.findById(req.getPackageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Package not found"));
            basePrice = pkg.getFinalPrice();
        } else if (req.getVehicleId() != null) {
            vehicle = vehicleRepository.findById(req.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
            if (!"AVAILABLE".equalsIgnoreCase(vehicle.getStatus().toString())) {
                throw new BadRequestException("Vehicle is not available");
            }
            basePrice = vehicle.getPerDayPrice().multiply(BigDecimal.valueOf(req.getDays()));
        } else {
            throw new BadRequestException("Either vehicleId or packageId is required");
        }

        BigDecimal discount = BigDecimal.ZERO;
        if (req.getPromoCode() != null && !req.getPromoCode().isEmpty()) {
            PromoCode promo = promoCodeRepository.findByCodeAndIsActiveTrue(req.getPromoCode().trim().toUpperCase())
                    .orElseThrow(() -> new BadRequestException("Invalid promo code"));

            if (promo.getValidUntil() != null && promo.getValidUntil().isBefore(LocalDate.now())) {
                throw new BadRequestException("Promo code expired");
            }

            if (promo.getDiscountType() == PromoCode.DiscountType.PERCENTAGE) {
                discount = basePrice.multiply(promo.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                if (promo.getMaxDiscount() != null && discount.compareTo(promo.getMaxDiscount()) > 0) {
                    discount = promo.getMaxDiscount();
                }
            } else {
                discount = promo.getDiscountValue();
            }
            promo.setUsedCount(promo.getUsedCount() + 1);
            promoCodeRepository.save(promo);
        }

        BigDecimal totalPrice = basePrice.subtract(discount);
        if (totalPrice.compareTo(BigDecimal.ZERO) < 0) totalPrice = BigDecimal.ZERO;

        VehicleBooking booking = VehicleBooking.builder()
                .userId(user.getId())
                .vehicleId(req.getVehicleId())
                .driverId(req.getDriverId())
                .packageId(req.getPackageId())
                .bookingType(req.getBookingType())
                .destination(req.getDestination())
                .pickupLocation(req.getPickupLocation())
                .dropoffLocation(req.getDropoffLocation())
                .pickupDate(req.getPickupDate())
                .returnDate(req.getReturnDate())
                .days(req.getDays())
                .basePrice(basePrice)
                .discountAmount(discount)
                .totalPrice(totalPrice)
                .promoCode(req.getPromoCode())
                .specialRequest(req.getSpecialRequest())
                .status(VehicleBooking.BookingStatus.PENDING)
                .build();

        VehicleBooking saved = bookingRepository.save(booking);

        if (vehicle != null) {
            try { vehicle.setStatus(Vehicle.VehicleStatus.valueOf("BOOKED")); } catch(Exception e) { vehicle.setStatus(Vehicle.VehicleStatus.BOOKED); }
            vehicleRepository.save(vehicle);
        }
        if (driver != null) {
            try { driver.setStatus(Driver.DriverStatus.valueOf("ON_TRIP")); } catch(Exception e) { driver.setStatus(Driver.DriverStatus.ON_TRIP); }
            driverRepository.save(driver);
        }

        // 🚀 Send Email
        try {
            String details = (vehicle != null ? "Vehicle: " + vehicle.getVehicleNumber() : "Package: " + pkg.getName()) +
                             "\nPickup: " + saved.getPickupDate() + "\nReturn: " + saved.getReturnDate();
            emailService.sendBookingConfirmation(
                user.getEmail(),
                user.getName(),
                saved.getId(),
                details
            );
        } catch (Exception e) {
            System.err.println("Vehicle Email Error: " + e.getMessage());
        }

        return ResponseEntity.ok(ApiResponse.success("Booking created", enrich(saved)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<VehicleBookingResponse>>> myBookings(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<VehicleBookingResponse> list = bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::enrich).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<VehicleBookingResponse>>> getAll() {
        List<VehicleBookingResponse> list = bookingRepository.findAll()
                .stream().map(this::enrich).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<VehicleBookingResponse>> updateStatus(
            @PathVariable Long id, @RequestParam String status) {
        VehicleBooking b = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        try {
            b.setStatus(VehicleBooking.BookingStatus.valueOf(status.toUpperCase()));
            if (status.equalsIgnoreCase("CANCELLED") || status.equalsIgnoreCase("COMPLETED")) {
                releaseResources(b);
            }
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status");
        }
        return ResponseEntity.ok(ApiResponse.success("Status updated", enrich(bookingRepository.save(b))));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<VehicleBookingResponse>> cancel(
            @PathVariable Long id, Authentication auth) {
        VehicleBooking b = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!b.getUserId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new BadRequestException("Not authorized");
        }

        b.setStatus(VehicleBooking.BookingStatus.CANCELLED);
        releaseResources(b);
        return ResponseEntity.ok(ApiResponse.success("Booking cancelled", enrich(bookingRepository.save(b))));
    }

    private void releaseResources(VehicleBooking b) {
        if (b.getVehicleId() != null) {
            vehicleRepository.findById(b.getVehicleId()).ifPresent(v -> {
                try { v.setStatus(Vehicle.VehicleStatus.valueOf("AVAILABLE")); } catch(Exception e) {}
                vehicleRepository.save(v);
            });
        }
        if (b.getDriverId() != null) {
            driverRepository.findById(b.getDriverId()).ifPresent(d -> {
                try { d.setStatus(Driver.DriverStatus.valueOf("AVAILABLE")); } catch(Exception e) {}
                driverRepository.save(d);
            });
        }
    }

    private VehicleBookingResponse enrich(VehicleBooking b) {
        VehicleBookingResponse r = VehicleBookingResponse.fromEntity(b);
        userRepository.findById(b.getUserId()).ifPresent(u -> r.setUserName(u.getName()));
        if (b.getVehicleId() != null) vehicleRepository.findById(b.getVehicleId()).ifPresent(v -> r.setVehicleNumber(v.getVehicleNumber()));
        if (b.getDriverId() != null) driverRepository.findById(b.getDriverId()).ifPresent(d -> r.setDriverName(d.getName()));
        if (b.getPackageId() != null) packageRepository.findById(b.getPackageId()).ifPresent(p -> r.setPackageName(p.getName()));
        return r;
    }
}
