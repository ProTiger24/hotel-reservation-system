package com.yourhotel.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "guest_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuestNote extends BaseEntity {

    @Column(name = "guest_id", nullable = false)
    private Long guestId;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String note;

    @Column(name = "is_internal", nullable = false)
    @Builder.Default
    private Boolean isInternal = true;
}
