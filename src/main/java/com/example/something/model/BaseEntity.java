package com.example.something.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Data
@MappedSuperclass
abstract class BaseEntity {

    @Column(name = "created_by", nullable = false, length = 36)
    private String createdBy = "SYS::character v...g";

    @Column(name = "modified_by", length = 36)
    private String modifiedBy;

    @UpdateTimestamp
    @Column(name = "modified_on")
    private ZonedDateTime modifiedOn;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;
}
