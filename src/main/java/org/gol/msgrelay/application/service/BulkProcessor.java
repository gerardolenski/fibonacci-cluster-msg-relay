package org.gol.msgrelay.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.msgrelay.application.api.RelayResult;
import org.gol.msgrelay.domain.NotificationPort;
import org.gol.msgrelay.domain.OutboxPort;
import org.gol.msgrelay.domain.model.MessageId;
import org.gol.msgrelay.domain.model.OutboxMessage;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class BulkProcessor {

    private final OutboxPort outboxPort;
    private final NotificationPort notificationPort;

    @Transactional
    RelayResult processNextBulk() {
        var bulk = outboxPort.nextBulk();
        if (bulk.isEmpty()) {
            log.debug("No bulk to process");
            return new RelayResult(0);
        }

        log.debug("Processing bulk: size={}, messageIds={}", bulk.size(), bulk.stream()
                .map(OutboxMessage::id)
                .map(MessageId::value)
                .toList());
        var notifiedIds = notificationPort.notify(bulk);
        outboxPort.setDelivered(notifiedIds);
        return new RelayResult(notifiedIds.size());
    }
}
