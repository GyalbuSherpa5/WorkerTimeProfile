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
@Table(name = "wfm_terminalgroup_worker_assignment")
@IdClass(TerminalGroupWorkerAssignment.TerminalGroupWorkerAssignmentPK.class)
public class TerminalGroupWorkerAssignment extends BaseEntity {

    @Id
    @Column(name = "worker_assignment_id", columnDefinition = "uuid")
    private UUID workerAssignmentId = UUID.randomUUID();

    @Id
    @Column(name = "organization_oid", length = 32)
    private String organizationOid;

    @Id
    @Column(name = "created_on")
    private ZonedDateTime createdOn;

    @Column(name = "terminalgroup_id", columnDefinition = "uuid")
    private UUID terminalgroupId;

    @Column(name = "worker_id", columnDefinition = "uuid")
    private UUID workerId;

    @PrePersist
    protected void onCreate() {
        if (createdOn == null) {
            createdOn = ZonedDateTime.now();
        }
    }

    // Composite Primary Key Class
    @Data
    public static class TerminalGroupWorkerAssignmentPK implements Serializable {
        private UUID workerAssignmentId;
        private String organizationOid;
        private ZonedDateTime createdOn;
    }
}
