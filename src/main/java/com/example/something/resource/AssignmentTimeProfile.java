package com.example.something.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentTimeProfile {
    private TimeOffPolicyCode timeOffPolicyCode;
    private Boolean supervisorIndicator;
    private TimeSupervisor timeSupervisor;
    private String badgeNumber;
    private Boolean allowBadgeTimepunchIndicator;
    private String timeZoneCode;
    private List<HomeLaborAllocation> homeLaborAllocations = new ArrayList<>();
    private String timeEntryPlanID;
}
