package org.gol.msgrelay.domain;

import org.gol.msgrelay.domain.model.MessageId;
import org.gol.msgrelay.domain.model.OutboxMessage;

import java.util.List;

/**
 * The secondary port to implement integration with messages outbox.
 */
public interface OutboxPort {

    /**
     * Retrieves the bulk of new messages to deliver.
     */
    List<OutboxMessage> nextBulk();

    /**
     * Set given messages as delivered.
     */
    void setDelivered(List<MessageId> ids);
}
