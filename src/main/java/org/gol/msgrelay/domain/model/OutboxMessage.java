package org.gol.msgrelay.domain.model;

import lombok.Builder;

import java.util.List;

@Builder
public record OutboxMessage(MessageId id, MessageBody body, List<MessageHeader> headers) {
}
