package org.gol.msgrelay.infrastructure.db;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity(name = "OutboxMsgHeader")
@Table(name = "outbox_header")
class OutboxMsgHeaderEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "hdr_key", length = 50)
    private String key;

    @Column(name = "hdr_value", length = 1024)
    private String value;
}
