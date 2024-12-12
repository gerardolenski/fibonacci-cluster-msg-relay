package org.gol.msgrelay.infrastructure.db;

import lombok.extern.slf4j.Slf4j;
import org.gol.msgrelay.domain.OutboxPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
class OutboxJpaAdapterConfig {

    @Bean
    OutboxPort outboxJpaAdapterPort(@Value("${outbox.bulk-size}") int bulkSize,
                                    OutboxMsgRepository msgRepository) {
        log.info("Creating outboxJpaAdapterPort with configuration: bulkSize={}", bulkSize);
        return new OutboxJpaAdapter(bulkSize, msgRepository);
    }
}
