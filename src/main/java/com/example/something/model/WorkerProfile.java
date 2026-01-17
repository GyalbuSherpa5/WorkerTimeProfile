package com.example.something.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("wfm_worker_profile")
public class WorkerProfile extends BaseEntity {

    @Id
    @Column("worker_profile_id")
    private UUID workerProfileId;

    @Column("organization_oid")
    private String organizationOid;

    @Column("associate_oid")
    private UUID associateOid;

    @Column("worker_id")
    private UUID workerId;

    @Column("given_name")
    private String givenName;

    @Column("family_name")
    private String familyName;

    @Column("badge_numbers")
    private String badgeNumbers;
}
