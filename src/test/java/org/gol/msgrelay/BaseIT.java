package org.gol.msgrelay;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.activemq.ArtemisContainer;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseIT {

    static ArtemisContainer artemis = new ArtemisContainer("apache/activemq-artemis:2.37.0-alpine");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        artemis.start();
        registry.add("spring.artemis.broker-url", artemis::getBrokerUrl);
    }
}
