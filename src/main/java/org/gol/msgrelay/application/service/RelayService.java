package org.gol.msgrelay.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gol.msgrelay.application.api.RelayPort;
import org.gol.msgrelay.application.api.RelayResult;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * The application service orchestrating the messages delivery process.
 *
 * @implSpec It processes all new messages located in outbox repository in bulks. The process is finished when next bulk
 * contains no messages.
 */
@Slf4j
@Service
@RequiredArgsConstructor
class RelayService implements RelayPort {

    private final BulkProcessor bulkProcessor;

    @Override
    public RelayResult deliver() {
        return Stream.generate(bulkProcessor::processNextBulk)
                .takeWhile(r -> r.deliveredCount() > 0)
                .reduce(new RelayResult(0), this::sumResults);
    }

    RelayResult sumResults(RelayResult r1, RelayResult r2) {
        return new RelayResult(r1.deliveredCount() + r2.deliveredCount());
    }
}
