package org.gol.msgrelay.infrastructure.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.msgrelay.domain.OutboxPort;
import org.gol.msgrelay.domain.model.MessageBody;
import org.gol.msgrelay.domain.model.MessageHeader;
import org.gol.msgrelay.domain.model.MessageId;
import org.gol.msgrelay.domain.model.OutboxMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@Slf4j
@RequiredArgsConstructor
@Transactional(propagation = MANDATORY)
class OutboxJpaAdapter implements OutboxPort {

    private final int bulkSize;
    private final OutboxMsgRepository msgRepo;

    @Override
    public List<OutboxMessage> nextBulk() {
        return msgRepo.findByIsDeliveredFalseOrderByCreatedAt(Pageable.ofSize(bulkSize)).stream()
                .map(this::toDomainRepresentation)
                .toList();
    }

    @Override
    public void setDelivered(List<MessageId> ids) {
        log.debug("Setting messages as delivered: ids={}", ids.stream().map(MessageId::value).toList());
        msgRepo.setAsDelivered(ids.stream().map(MessageId::value).toList());
    }

    OutboxMessage toDomainRepresentation(OutboxMsgEntity entity) {
        return OutboxMessage.builder()
                .id(new MessageId(entity.getId()))
                .headers(entity.getHeaders().stream()
                        .map(h -> new MessageHeader(h.getKey(), h.getValue()))
                        .toList())
                .body(new MessageBody(entity.getData()))
                .build();
    }
}
