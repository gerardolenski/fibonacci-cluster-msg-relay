package org.gol.msgrelay.infrastructure.jms;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.gol.msgrelay.BaseIT;
import org.gol.msgrelay.domain.NotificationPort;
import org.gol.msgrelay.domain.model.MessageBody;
import org.gol.msgrelay.domain.model.MessageHeader;
import org.gol.msgrelay.domain.model.OutboxMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gol.msgrelay.domain.model.MessageId.generateMsgId;

@TestPropertySource(properties = "mq.job-queue-name=AmqNotificationAdapterTest")
class AmqNotificationAdapterTest extends BaseIT {

    public static final int MAX_TIMEOUT = 10_000;
    @Autowired
    private NotificationPort sut;
    @Qualifier("jmsConnectionFactory")
    @Autowired
    private ConnectionFactory connectionFactory;
    @Value("${mq.job-queue-name}")
    private String queueName;

    @Test
    @DisplayName("should correctly send all messages [positive]")
    void shouldSendAllMessages() {
        //given
        var messages = generateMessages(10);

        //when
        var result = sut.notify(messages);

        //then
        assertThat(result).containsExactlyInAnyOrderElementsOf(messages.stream()
                .map(OutboxMessage::id)
                .toList());
        assertMessagesDeliveredToQueue(messages);
    }

    List<OutboxMessage> generateMessages(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> OutboxMessage.builder()
                        .id(generateMsgId())
                        .body(new MessageBody("msg " + i))
                        .headers(List.of(new MessageHeader("key", "val " + i)))
                        .build())
                .toList();
    }

    void assertMessagesDeliveredToQueue(List<OutboxMessage> messages) {
        var jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setReceiveTimeout(MAX_TIMEOUT);
        var receivedMessages = Stream.generate(() -> jmsTemplate.receive(queueName))
                .limit(messages.size())
                .toList();
        assertThat(receivedMessages.stream().map(this::extractBody))
                .containsExactlyInAnyOrderElementsOf(messages.stream()
                        .map(OutboxMessage::body)
                        .map(MessageBody::value).toList());
        assertThat(receivedMessages.stream().map(this::extractHeader))
                .containsExactlyInAnyOrderElementsOf(messages.stream()
                        .map(OutboxMessage::headers)
                        .map(List::getFirst)
                        .map(MessageHeader::value)
                        .toList());
    }

    String extractBody(Message msg) {
        try {
            return msg.getBody(String.class);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    String extractHeader(Message msg) {
        try {
            return msg.getStringProperty("key");
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}