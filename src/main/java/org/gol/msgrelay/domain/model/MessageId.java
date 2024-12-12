package org.gol.msgrelay.domain.model;

import java.util.UUID;

import static java.util.UUID.randomUUID;

public record MessageId(UUID value) {

    public static MessageId generateMsgId() {
        return new MessageId(randomUUID());
    }
}
