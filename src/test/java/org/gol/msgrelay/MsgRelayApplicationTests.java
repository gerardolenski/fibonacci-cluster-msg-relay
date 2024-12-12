package org.gol.msgrelay;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MsgRelayApplicationTests extends BaseIT {

    @Autowired
    private Environment env;

    @Test
    void contextLoads() {
        assertThat(env).isNotNull();
    }

}
