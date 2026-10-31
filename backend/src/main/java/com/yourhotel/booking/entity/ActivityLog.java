package com.yourhotel.booking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "activity_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog extends BaseEntity {

    @Column(name = "actor_id")
    private Long actorId;

    @Column(name = "target_user_id")
    private Long targetUserId;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(length = 50)
    private String entity;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(columnDefinition = "TEXT")
    private String metadata;
}
