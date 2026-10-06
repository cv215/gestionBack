package org.gestion.gestionstock.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AbstractEntity implements Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "creationDate", nullable = false, updatable = false)
    @CreatedDate
    private Instant creationDate;

    @Column(name = "lastUpdateDate")
    @LastModifiedDate
    private Instant lastUpdateDate;
}
