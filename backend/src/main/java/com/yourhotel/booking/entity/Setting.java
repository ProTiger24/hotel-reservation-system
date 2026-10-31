package com.yourhotel.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setting {

    @Id
    @Column(name = "setting_key", length = 100, nullable = false)
    private String key; // e.g., "hotel_name", "vat_percentage"

    @Column(name = "setting_value", columnDefinition = "TEXT")
    private String value;

    @Column(name = "category", length = 50)
    private String category; // GENERAL, BOOKING, PAYMENT, etc.

    @Column(name = "data_type", length = 20)
    private String dataType; // STRING, BOOLEAN, NUMBER, TEXT

    @Column(name = "label", length = 200)
    private String label; // Human readable label

    @Column(name = "description", length = 500)
    private String description;
}
