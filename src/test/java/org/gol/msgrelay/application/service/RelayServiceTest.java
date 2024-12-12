package org.gol.msgrelay.application.service;

import jakarta.jms.Message;
import org.gol.msgrelay.BaseIT;
import org.gol.msgrelay.infrastructure.db.OutboxMsgRepositoryDelegate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@TestPropertySource(properties = {"spring.jpa.show-sql=true", "mq.job-queue-name=RelayServiceTest"})
class RelayServiceTest extends BaseIT {

    public static final Duration MAX_TIMEOUT = ofSeconds(10);
    @Autowired
    private RelayService sut;
    @Autowired
    private OutboxMsgRepositoryDelegate msgRepository;
    @Autowired
    private JobMessagesConsumer jobMessagesConsumer;

    @TestConfiguration
    static class RelayServiceTestContextConfiguration {

        @Bean
        JobMessagesConsumer jobMessagesConsumer() {
            return new JobMessagesConsumer();
        }
    }

    @BeforeEach
    void setUp() {
        msgRepository.deleteAll();
        jobMessagesConsumer.messagesRepo.clear();
    }

    @Test
    @DisplayName("should process then stop when no new messages are in outbox [positive]")
    void processingNoNewMessages() {
        //when
        var res = sut.deliver();

        //then
        assertThat(res.deliveredCount()).isZero();
    }

    @Test
    @DisplayName("should process entire bulk then stop [positive]")
    void processingOneBulk() {
        //given 5 messages
        var messagesCount = 5;
        IntStream.rangeClosed(1, messagesCount)
                .forEach(msgRepository::persistNewMessage);

        //when
        var res = sut.deliver();

        //then
        assertThat(res.deliveredCount()).isEqualTo(messagesCount);

        //when processing another time
        res = sut.deliver();

        //then
        assertThat(res.deliveredCount()).isZero();

        //then messages were delivered
        await().atMost(MAX_TIMEOUT).until(() -> jobMessagesConsumer.messagesRepo.size() == messagesCount);
        assertThat(jobMessagesConsumer.messagesRepo).hasSize(messagesCount);
    }

    @Test
    @DisplayName("should process all remaining bulks then stop [positive]")
    void processingManyBulks() {
        //given 26 messages
        var messagesCount = 26;
        IntStream.rangeClosed(1, messagesCount)
                .forEach(msgRepository::persistNewMessage);

        //when
        var res = sut.deliver();

        //then
        assertThat(res.deliveredCount()).isEqualTo(messagesCount);

        //when processing another time
        res = sut.deliver();

        //then
        assertThat(res.deliveredCount()).isZero();

        //then messages were delivered
        await().atMost(MAX_TIMEOUT).until(() -> jobMessagesConsumer.messagesRepo.size() == messagesCount);
        assertThat(jobMessagesConsumer.messagesRepo).hasSize(messagesCount);
    }

    static class JobMessagesConsumer {

        final List<Message> messagesRepo = new LinkedList<>();

        @JmsListener(destination = "RelayServiceTest", concurrency = "1-1")
        public void receive(Message message) {
            messagesRepo.add(message);
        }
    }
}