package com.example.something.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("wfm_terminalgroup_worker_assignment")
public class TerminalGroupWorkerAssignment extends BaseEntity {

    @Id
    @Column("worker_assignment_id")
    private UUID workerAssignmentId;

    @Column("organization_oid")
    private String organizationOid;

    @Column("terminalgroup_id")
    private UUID terminalgroupId;

    @Column("worker_id")
    private UUID workerId;
}
