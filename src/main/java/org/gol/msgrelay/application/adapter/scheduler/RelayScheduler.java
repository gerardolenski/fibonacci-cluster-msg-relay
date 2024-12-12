package org.gol.msgrelay.application.adapter.scheduler;

import lombok.RequiredArgsConstructor;
import org.gol.msgrelay.application.api.RelayPort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Primary adapter that processes messages by internal scheduler.
 */
@Service
@RequiredArgsConstructor
class RelayScheduler {

    private final RelayPort relayPort;

    @Scheduled(fixedRateString = "${scheduler.fixed-rate}", initialDelayString = "${scheduler.initial-delay}")
    public void deliverMessages() {
        relayPort.deliver();
    }
}
