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
@Table(name = "wfm_worker_badge")
@IdClass(WorkerBadge.WorkerBadgePK.class)
public class WorkerBadge extends BaseEntity {

    @Id
    @Column(name = "worker_badge_id", columnDefinition = "uuid")
    private UUID workerBadgeId = UUID.randomUUID();

    @Id
    @Column(name = "organization_oid", length = 16)
    private String organizationOid;

    @Id
    @Column(name = "created_on")
    private ZonedDateTime createdOn;

    @Column(name = "associate_oid", length = 36)
    private String associateOid;

    @Column(name = "work_assignment_id", length = 36)
    private String workAssignmentId;

    @Column(name = "badge_number", length = 16)
    private String badgeNumber;

    @PrePersist
    protected void onCreate() {
        if (createdOn == null) {
            createdOn = ZonedDateTime.now();
        }
    }

    // Composite Primary Key Class
    @Data
    public static class WorkerBadgePK implements Serializable {
        private UUID workerBadgeId;
        private String organizationOid;
        private ZonedDateTime createdOn;
    }
}
