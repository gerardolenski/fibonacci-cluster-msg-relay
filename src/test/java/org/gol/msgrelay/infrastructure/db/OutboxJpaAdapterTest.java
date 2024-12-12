package org.gol.msgrelay.infrastructure.db;

import lombok.RequiredArgsConstructor;
import org.gol.msgrelay.BaseIT;
import org.gol.msgrelay.domain.OutboxPort;
import org.gol.msgrelay.domain.model.MessageBody;
import org.gol.msgrelay.domain.model.MessageHeader;
import org.gol.msgrelay.domain.model.OutboxMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static java.util.stream.IntStream.rangeClosed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.gol.msgrelay.infrastructure.db.EntityUtils.createEntity;

class OutboxJpaAdapterTest extends BaseIT {

    @Autowired
    private OutboxMsgRepository repository;
    @Autowired
    private Config.DeliverUseCase deliverUseCase;
    @Autowired
    private OutboxPort sut;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @TestConfiguration
    static class Config {

        @Service
        @RequiredArgsConstructor
        static class DeliverUseCase {

            private final OutboxPort outboxPort;

            @Transactional
            public List<OutboxMessage> processAndDeliver() {
                var bulk = outboxPort.nextBulk();
                outboxPort.setDelivered(bulk.stream().map(OutboxMessage::id).toList());
                return bulk;
            }
        }
    }

    @Test
    @DisplayName("should correctly process bulks [positive]")
    void bulkProcessing() {
        //given
        var now = Instant.now();
        rangeClosed(1, 12)
                .mapToObj(nr -> createEntity(nr, now))
                .forEach(repository::save);

        //when processing 1st bulk
        var bulk = deliverUseCase.processAndDeliver();

        //then
        assertThat(bulk).hasSize(5);
        assertThat(bulk).extracting(OutboxMessage::body)
                .containsExactly(new MessageBody("msg 1"), new MessageBody("msg 2"), new MessageBody("msg 3"), new MessageBody("msg 4"), new MessageBody("msg 5"));
        assertThat(bulk).allSatisfy(msg ->
                assertThat(msg.headers()).containsExactlyInAnyOrder(new MessageHeader("key1", "val1"), new MessageHeader("key2", "val2")));

        //when processing 2nd bulk
        bulk = deliverUseCase.processAndDeliver();

        //then
        assertThat(bulk).hasSize(5);
        assertThat(bulk).extracting(OutboxMessage::body)
                .containsExactly(new MessageBody("msg 6"), new MessageBody("msg 7"), new MessageBody("msg 8"), new MessageBody("msg 9"), new MessageBody("msg 10"));
        assertThat(bulk).allSatisfy(msg ->
                assertThat(msg.headers()).containsExactlyInAnyOrder(new MessageHeader("key1", "val1"), new MessageHeader("key2", "val2")));

        //when processing 3rd bulk
        bulk = deliverUseCase.processAndDeliver();

        //then
        assertThat(bulk).hasSize(2);
        assertThat(bulk).extracting(OutboxMessage::body)
                .containsExactly(new MessageBody("msg 11"), new MessageBody("msg 12"));
        assertThat(bulk).allSatisfy(msg ->
                assertThat(msg.headers()).containsExactlyInAnyOrder(new MessageHeader("key1", "val1"), new MessageHeader("key2", "val2")));

        //when processing 4th bulk
        bulk = deliverUseCase.processAndDeliver();

        //then
        assertThat(bulk).hasSize(0);
    }
}