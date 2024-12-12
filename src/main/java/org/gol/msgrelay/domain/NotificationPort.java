package org.gol.msgrelay.domain;

import org.gol.msgrelay.domain.model.MessageId;
import org.gol.msgrelay.domain.model.OutboxMessage;

import java.util.List;

public interface NotificationPort {

    List<MessageId> notify(List<OutboxMessage> messages);
}
