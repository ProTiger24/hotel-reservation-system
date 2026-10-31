package com.yourhotel.booking.dto.response;

import com.yourhotel.booking.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long roomId;
    private String roomNumber;
    private String roomType;
    private String roomImageUrl;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer nights;
    private BigDecimal totalPrice;
    private Integer guestCount;
    private String specialRequest;
    private String status;
    private String paymentStatus;
    private Integer loyaltyPointsEarned;
    private LocalDateTime createdAt;

    public static BookingResponse fromEntity(Booking booking) {
        long nights = java.time.temporal.ChronoUnit.DAYS.between(booking.getCheckIn(), booking.getCheckOut());
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .userName(booking.getUser().getName())
                .userEmail(booking.getUser().getEmail())
                .roomId(booking.getRoom().getId())
                .roomNumber(booking.getRoom().getRoomNumber())
                .roomType(booking.getRoom().getRoomType().getName())
                .roomImageUrl(booking.getRoom().getImageUrl())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .nights((int) nights)
                .totalPrice(booking.getTotalPrice())
                .guestCount(booking.getGuestCount())
                .specialRequest(booking.getSpecialRequest())
                .status(booking.getStatus().name())
                .paymentStatus(booking.getPaymentStatus() != null ? booking.getPaymentStatus().name() : "PENDING")
                .loyaltyPointsEarned(booking.getLoyaltyPointsEarned())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
