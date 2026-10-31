package com.yourhotel.booking.dto.response;

import com.yourhotel.booking.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private Boolean enabled;
    private Boolean emailVerified;
    private Integer loyaltyPoints;
    private String tier;
    private LocalDateTime createdAt;

    // Calculated stats (defaults to 0 for now)
    private Long totalBookings;
    private Long completedBookings;
    private Long cancelledBookings;
    private Long upcomingBookings;
    private BigDecimal totalSpent;
    private BigDecimal averageBooking;
    private LocalDateTime lastStayDate;
    private Boolean currentlyStaying;

    public static GuestResponse fromEntity(User user) {
        return GuestResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole() != null ? user.getRole().name() : "USER")
                .enabled(user.getEnabled() != null ? user.getEnabled() : true)
                .emailVerified(user.getEmailVerified() != null ? user.getEmailVerified() : false)
                .loyaltyPoints(user.getLoyaltyPoints() != null ? user.getLoyaltyPoints() : 0)
                .tier(user.getTier() != null ? user.getTier() : "BRONZE")
                .createdAt(user.getCreatedAt())
                // Default values for calculated stats
                .totalBookings(0L)
                .completedBookings(0L)
                .cancelledBookings(0L)
                .upcomingBookings(0L)
                .totalSpent(BigDecimal.ZERO)
                .averageBooking(BigDecimal.ZERO)
                .lastStayDate(null)
                .currentlyStaying(false)
                .build();
    }
}
