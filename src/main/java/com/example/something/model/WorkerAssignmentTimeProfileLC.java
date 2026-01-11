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
@Table(name = "wfm_worker_assignment_time_profile_lc")
@IdClass(WorkerAssignmentTimeProfileLC.WorkerAssignmentTimeProfileLCPK.class)
public class WorkerAssignmentTimeProfileLC extends BaseEntity {

    @Id
    @Column(name = "worker_assignment_time_profile_lc_id", columnDefinition = "uuid")
    private UUID workerAssignmentTimeProfileLcId = UUID.randomUUID();

    @Id
    @Column(name = "organization_oid", length = 16)
    private String organizationOid;

    @Column(name = "created_on")
    private ZonedDateTime createdOn;

    @Column(name = "worker_assignment_profile_id", columnDefinition = "uuid")
    private UUID workerAssignmentProfileId;

    @Column(name = "allocation_code", length = 36)
    private String allocationCode;

    @Column(name = "allocation_name", length = 100)
    private String allocationName;

    @Column(name = "allocation_type_code", length = 36)
    private String allocationTypeCode;

    @Column(name = "allocation_type_name", length = 100)
    private String allocationTypeName;

    @Column(name = "allocation_category_code", length = 36)
    private String allocationCategoryCode;

    @Column(name = "allocation_category_name", length = 100)
    private String allocationCategoryName;

    @PrePersist
    protected void onCreate() {
        if (createdOn == null) {
            createdOn = ZonedDateTime.now();
        }
    }

    // Composite Primary Key Class
    @Data
    public static class WorkerAssignmentTimeProfileLCPK implements Serializable {
        private UUID workerAssignmentTimeProfileLcId;
        private String organizationOid;
    }
}
