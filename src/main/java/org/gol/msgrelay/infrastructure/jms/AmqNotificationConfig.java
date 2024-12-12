package org.gol.msgrelay.infrastructure.jms;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import lombok.extern.slf4j.Slf4j;
import org.gol.msgrelay.domain.NotificationPort;
import org.gol.msgrelay.domain.model.OutboxMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

@Slf4j
@Configuration
class AmqNotificationConfig {

    @Bean
    NotificationPort amqNotificationPort(@Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory,
                                         @Value("${mq.job-queue-name}") String jobQueueName) {
        log.info("Initializing amqNotificationPort: jobQueueName={}", jobQueueName);
        return new AmqNotificationAdapter(initJmsTpl(connectionFactory), jobQueueName);
    }

    private JmsTemplate initJmsTpl(ConnectionFactory connectionFactory) {
        var tpl = new JmsTemplate(connectionFactory);
        tpl.setMessageConverter(new OutboxMessageConverter());
        return tpl;
    }

    static class OutboxMessageConverter implements MessageConverter {

        @Override
        public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
            return switch (object) {
                case OutboxMessage outboxMsg -> toOutboxMessage(outboxMsg, session);
                default -> throw new MessageConversionException("Unknown object type: " + object);
            };
        }

        private Message toOutboxMessage(OutboxMessage outboxMsg, Session session) throws JMSException {
            var amqMsg = session.createTextMessage(outboxMsg.body().value());
            for (var header : outboxMsg.headers())
                amqMsg.setStringProperty(header.key(), header.value());
            return amqMsg;
        }

        @Override
        public Object fromMessage(Message message) throws JMSException, MessageConversionException {
            throw new MessageConversionException("Not implemented");
        }
    }
}
