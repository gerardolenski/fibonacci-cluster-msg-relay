package org.gol.msgrelay.infrastructure.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.time.Instant.now;
import static org.gol.msgrelay.infrastructure.db.EntityUtils.createEntity;

@Service
@RequiredArgsConstructor
public class OutboxMsgRepositoryDelegate {

    private final OutboxMsgRepository repository;

    public void deleteAll() {
        repository.deleteAll();
    }

    public void persistNewMessage(int msgNr) {
        repository.save(createEntity(msgNr, now()));
    }

}
