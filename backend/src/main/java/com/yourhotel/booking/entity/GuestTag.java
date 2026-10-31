package com.yourhotel.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "guest_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestTag extends BaseEntity {

    @Column(name = "guest_id", nullable = false)
    private Long guestId;

    @Column(nullable = false, length = 50)
    private String tag;
}
