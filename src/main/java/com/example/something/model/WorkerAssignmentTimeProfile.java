package com.example.something.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Table("wfm_worker_assignment_time_profile")
public class WorkerAssignmentTimeProfile extends BaseEntity {

    @Id
    @Column("worker_assignment_profile_id")
    private UUID workerAssignmentProfileId;

    @Column("organization_oid")
    private String organizationOid;

    @Column("work_assignment_id")
    private UUID workAssignmentId;

    @Column("work_assignment_title")
    private String workAssignmentTitle;

    @Column("hire_date")
    private LocalDate hireDate;

    @Column("worker_profile_id")
    private UUID workerProfileId;

    @Column("assignment_status")
    private String assignmentStatus;

    @Column("time_zone")
    private String timeZone;

    @Column("badge_number")
    private String badgeNumber;

    @Column("geofencing_bypass_indicator")
    private Boolean geofencingBypassIndicator;

    @Column("business_communications")
    private JsonNode businessCommunications;

    @Column("has_hardware_clock_permission")
    private Boolean hasHardwareClockPermission;

    @Column("has_kiosk_app_permission")
    private Boolean hasKioskAppPermission;

    @Column("is_primary_assignment")
    private Boolean isPrimaryAssignment;

    @Column("home_labor_allocations")
    private JsonNode homeLaborAllocations;

    @Column("clock_actions")
    private JsonNode clockActions;

    @Column("is_supervisor")
    private Boolean isSupervisor;

    @Column("supervisor_information")
    private JsonNode supervisorInformation;
}
