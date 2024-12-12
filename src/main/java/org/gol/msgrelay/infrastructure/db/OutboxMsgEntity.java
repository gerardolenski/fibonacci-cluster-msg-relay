package org.gol.msgrelay.infrastructure.db;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.EAGER;

@Getter
@Setter
@ToString
@Entity(name = "OutboxMsg")
@Table(name = "outbox", indexes = {
        @Index(name = "om_is_delivered_idx", columnList = "is_delivered, created_at")
})
@EntityListeners(AuditingEntityListener.class)
class OutboxMsgEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @OneToMany(cascade = ALL, orphanRemoval = true, fetch = EAGER)
    @JoinColumn(name = "message_id")
    private List<OutboxMsgHeaderEntity> headers;

    @Column(name = "data", columnDefinition = "TEXT", nullable = false)
    private String data;

    @Column(name = "is_delivered", nullable = false)
    private Boolean isDelivered;

    @Column(name = "created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    private Instant modifiedAt;
}
