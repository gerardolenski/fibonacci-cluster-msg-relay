package org.gol.msgrelay.infrastructure.db;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

class EntityUtils {

   static OutboxMsgEntity createEntity(int msgNr, Instant timestamp) {
        OutboxMsgEntity entity = new OutboxMsgEntity();
        entity.setId(UUID.randomUUID());
        entity.setHeaders(List.of(createHeaderEntity("key1", "val1"), createHeaderEntity("key2", "val2")));
        entity.setData("msg " + msgNr);
        entity.setIsDelivered(false);
        entity.setCreatedAt(timestamp.plusMillis(msgNr));
        return entity;
    }

    static OutboxMsgHeaderEntity createHeaderEntity(String key, String value) {
        var entity = new OutboxMsgHeaderEntity();
        entity.setKey(key);
        entity.setValue(value);
        return entity;
    }
}
