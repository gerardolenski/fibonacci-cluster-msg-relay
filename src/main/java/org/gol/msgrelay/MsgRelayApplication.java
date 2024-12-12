package org.gol.msgrelay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@SpringBootApplication
public class MsgRelayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsgRelayApplication.class, args);
    }

    @Configuration
    @EnableScheduling
    @ConditionalOnProperty(value = "scheduler.is-enabled", havingValue = "true")
    static class SchedulingConfig {

    }
}
