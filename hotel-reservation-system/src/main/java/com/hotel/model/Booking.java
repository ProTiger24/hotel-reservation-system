package com.hotel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    
    @Column(nullable = false, length = 100)
    private String guestName;
    
    @Column(nullable = false, length = 100)
    private String guestEmail;
    
    @Column(nullable = false, length = 20)
    private String guestPhone;
    
    @Column(nullable = false)
    private LocalDate checkIn;
    
    @Column(nullable = false)
    private LocalDate checkOut;
    
    @Column(nullable = false)
    private Integer guests;
    
    @Column(nullable = false)
    private Double totalAmount;
    
    @Column(nullable = false)
    private Double advancePaid = 0.0;
    
    @Column(nullable = false)
    private Double remainingAmount = 0.0;
    
    @Column(nullable = false, length = 50)
    private String paymentMethod;
    
    @Column(length = 50)
    private String paymentStatus = "ADVANCE_PAID";
    
    @Column(length = 100)
    private String transactionId;
    
    @Column(nullable = false, length = 50)
    private String status = "CONFIRMED";
    
    @Column
    private LocalDate bookingDate = LocalDate.now();
    
    @Column
    private LocalDateTime checkInTime;
    
    @Column
    private LocalDateTime checkOutTime;
    
    @Column
    private Boolean isCheckedIn = false;
    
    @Column
    private Boolean isCheckedOut = false;
}
