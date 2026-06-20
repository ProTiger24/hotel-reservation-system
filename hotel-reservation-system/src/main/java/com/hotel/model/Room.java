package com.hotel.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Boolean available = true;

    @Column(length = 500)
    private String description;

    @Column(length = 500)
    private String amenities;
}