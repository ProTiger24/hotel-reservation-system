package com.yourhotel.booking.controller;

import com.yourhotel.booking.entity.Booking;
import com.yourhotel.booking.entity.User; // 🚀 FIX: Added missing import
import com.yourhotel.booking.entity.VehicleBooking;
import com.yourhotel.booking.repository.BookingRepository;
import com.yourhotel.booking.repository.UserRepository;
import com.yourhotel.booking.repository.VehicleBookingRepository;
import com.yourhotel.booking.service.BkashService;
import com.yourhotel.booking.service.EmailService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/payments/bkash")
public class BkashController {

    private final BkashService bkashService;
    private final BookingRepository bookingRepository;
    private final VehicleBookingRepository vehicleBookingRepository;
    private final UserRepository userRepository; // 🚀 Added
    private final EmailService emailService;

    public BkashController(BkashService bkashService, 
                           BookingRepository bookingRepository, 
                           VehicleBookingRepository vehicleBookingRepository,
                           UserRepository userRepository,
                           EmailService emailService) {
        this.bkashService = bkashService;
        this.bookingRepository = bookingRepository;
        this.vehicleBookingRepository = vehicleBookingRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> payload) {
        Long bookingId = Long.valueOf(payload.get("bookingId").toString());
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        String token = bkashService.grantToken();
        String amount = booking.getTotalPrice().toString();
        String invoiceNumber = "INV-" + booking.getId();
        String payerReference = "USER-" + booking.getUser().getId();

        Map<String, Object> response = bkashService.createPayment(token, amount, invoiceNumber, payerReference, bookingId, "HOTEL");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-vehicle")
    public ResponseEntity<?> createVehiclePayment(@RequestBody Map<String, Object> payload) {
        Long bookingId = Long.valueOf(payload.get("bookingId").toString());
        VehicleBooking booking = vehicleBookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Vehicle Booking not found"));
        String token = bkashService.grantToken();
        String amount = booking.getTotalPrice().toString();
        String invoiceNumber = "VINV-" + booking.getId();
        String payerReference = "USER-" + booking.getUserId();

        Map<String, Object> response = bkashService.createPayment(token, amount, invoiceNumber, payerReference, bookingId, "VEHICLE");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(
            @RequestParam(required = false) String paymentID, 
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String bookingId,
            @RequestParam(required = false) String type) {
        
        if ("success".equals(status) && paymentID != null) {
            String token = bkashService.grantToken();
            Map<String, Object> executeResponse = bkashService.executePayment(token, paymentID);

            if ("Completed".equals(executeResponse.get("transactionStatus"))) {
                if (bookingId != null) {
                    Long id = Long.valueOf(bookingId);
                    
                    if ("VEHICLE".equals(type)) {
                        VehicleBooking vb = vehicleBookingRepository.findById(id).orElse(null);
                        if (vb != null) {
                            vb.setStatus(VehicleBooking.BookingStatus.CONFIRMED);
                            vehicleBookingRepository.save(vb);
                            
                            // 🚀 Send Payment Success Email for Vehicle
                            try {
                                User vbUser = userRepository.findById(vb.getUserId()).orElse(null);
                                if (vbUser != null) {
                                    emailService.sendPaymentSuccess(
                                        vbUser.getEmail(),
                                        vbUser.getName(),
                                        vb.getId(),
                                        vb.getTotalPrice().toString()
                                    );
                                }
                            } catch (Exception e) {
                                System.err.println("Vehicle Payment Email Error: " + e.getMessage());
                            }
                        }
                    } else {
                        Booking booking = bookingRepository.findById(id).orElse(null);
                        if (booking != null) {
                            booking.setPaymentStatus(Booking.PaymentStatus.PAID);
                            booking.setStatus(Booking.BookingStatus.CONFIRMED);
                            bookingRepository.save(booking);
                            
                            // 🚀 Send Email Safely
                            try {
                                if (booking.getUser() != null) {
                                    emailService.sendPaymentSuccess(
                                        booking.getUser().getEmail(), 
                                        booking.getUser().getName(), 
                                        booking.getId(), 
                                        booking.getTotalPrice().toString()
                                    );
                                }
                            } catch (Exception e) {
                                System.err.println("Email Error: " + e.getMessage());
                            }
                        }
                    }
                    
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(URI.create("http://localhost:3000/payment-success.html?bookingId=" + id + "&type=" + type));
                    return new ResponseEntity<>(headers, HttpStatus.FOUND);
                }
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:3000/my-bookings.html?status=failed"));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
