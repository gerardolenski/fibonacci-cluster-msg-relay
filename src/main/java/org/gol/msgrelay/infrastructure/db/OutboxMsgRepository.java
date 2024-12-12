package org.gol.msgrelay.infrastructure.db;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

interface OutboxMsgRepository extends JpaRepository<OutboxMsgEntity, UUID> {

    Slice<OutboxMsgEntity> findByIsDeliveredFalseOrderByCreatedAt(Pageable pageable);

    @Modifying
    @Query("""
            update OutboxMsg m
             set m.isDelivered = true
             where m.id in :ids
            """)
    void setAsDelivered(List<UUID> ids);
}
