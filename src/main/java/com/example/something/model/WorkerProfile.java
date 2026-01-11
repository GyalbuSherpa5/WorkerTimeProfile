package com.example.something.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "wfm_worker_profile")
@IdClass(WorkerProfile.WorkerProfilePK.class)
public class WorkerProfile extends BaseEntity {

    @Id
    @Column(name = "worker_profile_id", columnDefinition = "uuid")
    private UUID workerProfileId = UUID.randomUUID();

    @Id
    @Column(name = "organization_oid", length = 16)
    private String organizationOid;

    @Id
    @Column(name = "created_on")
    private ZonedDateTime createdOn;

    @Column(name = "associate_oid", length = 36)
    private String associateOid;

    @Column(name = "worker_id", length = 36)
    private String workerId;

    @Column(name = "given_name")
    private String givenName;

    @Column(name = "family_name")
    private String familyName;

    @PrePersist
    protected void onCreate() {
        if (createdOn == null) {
            createdOn = ZonedDateTime.now();
        }
    }

    // Composite Primary Key Class
    @Data
    public static class WorkerProfilePK implements Serializable {
        private UUID workerProfileId;
        private String organizationOid;
        private ZonedDateTime createdOn;
    }
}
