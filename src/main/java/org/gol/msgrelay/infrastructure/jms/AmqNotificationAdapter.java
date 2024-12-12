package org.gol.msgrelay.infrastructure.jms;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.gol.msgrelay.domain.NotificationPort;
import org.gol.msgrelay.domain.model.MessageId;
import org.gol.msgrelay.domain.model.OutboxMessage;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;

@Slf4j
@Builder
class AmqNotificationAdapter implements NotificationPort {

    private final JmsTemplate jmsTemplate;
    private final String queueName;

    @Override
    public List<MessageId> notify(List<OutboxMessage> messages) {
        return messages.stream()
                .map(this::send)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    private Optional<MessageId> send(OutboxMessage message) {
        try {
            log.debug("Sending message: {}", message);
            jmsTemplate.convertAndSend(new ActiveMQQueue(queueName), message);
            return Optional.of(message.id());
        } catch (Exception e) {
            log.error("Failed to send message {}", message, e);
            return empty();
        }
    }
}
