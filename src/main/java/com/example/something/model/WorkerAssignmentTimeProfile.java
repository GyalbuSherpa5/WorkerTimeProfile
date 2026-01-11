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
@Table(name = "wfm_worker_assignment_time_profile")
@IdClass(WorkerAssignmentTimeProfile.WorkerAssignmentTimeProfilePK.class)
public class WorkerAssignmentTimeProfile extends BaseEntity {

    @Id
    @Column(name = "worker_assignment_profile_id", columnDefinition = "uuid")
    private UUID workerAssignmentProfileId = UUID.randomUUID();

    @Id
    @Column(name = "organization_oid", length = 16)
    private String organizationOid;

    @Id
    @Column(name = "created_on")
    private ZonedDateTime createdOn;

    @Column(name = "work_assignment_id", length = 36)
    private String workAssignmentId;

    @Column(name = "worker_profile_id", columnDefinition = "uuid")
    private UUID workerProfileId;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "time_zone", length = 10)
    private String timeZone;

    @Column(name = "time_entry_plan_id", length = 16)
    private String timeEntryPlanId;

    @Column(name = "geofencing_bypass_indicator")
    private Boolean geofencingBypassIndicator;

    @Column(name = "business_communications", columnDefinition = "jsonb")
    private String businessCommunications;

    @Column(name = "has_hardware_clock_permission")
    private Boolean hasHardwareClockPermission;

    @Column(name = "has_kiosk_app_permission")
    private Boolean hasKioskAppPermission;

    @PrePersist
    protected void onCreate() {
        if (createdOn == null) {
            createdOn = ZonedDateTime.now();
        }
    }

    // Composite Primary Key Class
    @Data
    public static class WorkerAssignmentTimeProfilePK implements Serializable {
        private UUID workerAssignmentProfileId;
        private String organizationOid;
        private ZonedDateTime createdOn;
    }
}
