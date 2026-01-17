package com.example.something.service;

import com.example.something.model.WorkerAssignmentTimeProfile;
import com.example.something.model.WorkerProfile;
import com.example.something.repository.WorkerAssignmentTimeProfileRepository;
import com.example.something.repository.WorkerProfileRepository;
import com.example.something.resource.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerServiceImpl implements WorkerService {

    private final WorkerProfileRepository workerProfileRepository;
    private final WorkerAssignmentTimeProfileRepository assignmentRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public WorkerTimeProfileWrapper getWorkersByTerminalGroup(
            String orgId,
            UUID terminalGroupId,
            String givenName,
            String familyName,
            int page,
            int size,
            boolean includeInGroup,
            boolean isPrimaryAssignment) {

        // Step 1: Get workers
        List<WorkerProfile> workers;
        if (includeInGroup) {
            workers = workerProfileRepository.findWorkersByTerminalGroup(
                    orgId, terminalGroupId, givenName, familyName
            );
        } else {
            workers = workerProfileRepository.findWorkersNotInTerminalGroup(
                    orgId, terminalGroupId, givenName, familyName
            );
        }

        if (workers.isEmpty()) {
            return new WorkerTimeProfileWrapper();
        }

        // Step 2: Get worker profile IDs and worker IDs
        List<UUID> workerProfileIds = workers.stream()
                .map(WorkerProfile::getWorkerProfileId)
                .collect(Collectors.toList());

        // Step 3: Get all assignments for these workers
        List<WorkerAssignmentTimeProfile> assignments =
                assignmentRepository.findByWorkerProfileIds(workerProfileIds, isPrimaryAssignment);

        // Step 4: Group assignments by worker profile ID
        Map<UUID, List<WorkerAssignmentTimeProfile>> assignmentsByWorker = assignments.stream()
                .collect(Collectors.groupingBy(WorkerAssignmentTimeProfile::getWorkerProfileId));

        // Step 5: Build response DTOs
        List<WorkerTimeProfile> workerTimeProfiles = workers.stream()
                .map(worker -> buildWorkerTimeProfile(
                        worker,
                        assignmentsByWorker.getOrDefault(worker.getWorkerProfileId(), Collections.emptyList())))
                .collect(Collectors.toList());

        // Step 6: Apply pagination
        PageResponse<WorkerTimeProfile> pagedResult = paginateList(workerTimeProfiles, page, size);
        return new WorkerTimeProfileWrapper(pagedResult.getContent());
    }

    private WorkerTimeProfile buildWorkerTimeProfile(
            WorkerProfile worker,
            List<WorkerAssignmentTimeProfile> assignments) {

        // Build PersonProfile
        PersonName personName = new PersonName(
                worker.getGivenName(),
                worker.getFamilyName(),
                worker.getFamilyName() + ", " + worker.getGivenName()
        );
        PersonProfile personProfile = new PersonProfile(personName);

        // Build WorkAssignmentProfiles
        List<WorkAssignmentProfile> workAssignmentProfiles = assignments.stream()
                .map(this::mapToWorkAssignmentProfile)
                .collect(Collectors.toList());

        return new WorkerTimeProfile(
                worker.getAssociateOid() != null ? worker.getAssociateOid().toString() : null,
                personProfile,
                workAssignmentProfiles
        );
    }

    private WorkAssignmentProfile mapToWorkAssignmentProfile(WorkerAssignmentTimeProfile assignment) {
        WorkAssignmentProfile profile = new WorkAssignmentProfile();

        profile.setWorkAssignmentID(
                assignment.getWorkAssignmentId() != null ? assignment.getWorkAssignmentId().toString() : null
        );
        profile.setWorkAssignmentTitle(assignment.getWorkAssignmentTitle());
        profile.setAssignmentStatus(new AssignmentStatus(assignment.getAssignmentStatus()));

        // Build AssignmentTimeProfile
        AssignmentTimeProfile timeProfile = new AssignmentTimeProfile();
        timeProfile.setSupervisorIndicator(assignment.getIsSupervisor());
        timeProfile.setBadgeNumber(assignment.getBadgeNumber());
        timeProfile.setTimeZoneCode(assignment.getTimeZone());
        timeProfile.setAllowBadgeTimepunchIndicator(assignment.getHasHardwareClockPermission());

        if (assignment.getSupervisorInformation() != null && !assignment.getSupervisorInformation().isNull()) {
            try {
                TimeSupervisor supervisor = objectMapper.convertValue(assignment.getSupervisorInformation(), TimeSupervisor.class);
                timeProfile.setTimeSupervisor(supervisor);
            } catch (Exception e) {
                log.error(String.valueOf(e));
            }
        }

        if (assignment.getHomeLaborAllocations() != null && !assignment.getHomeLaborAllocations().isNull()) {
            try {
                List<HomeLaborAllocation> allocations = objectMapper.convertValue(
                        assignment.getHomeLaborAllocations(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, HomeLaborAllocation.class)
                );
                timeProfile.setHomeLaborAllocations(allocations);
            } catch (Exception e) {
                log.error(String.valueOf(e));
            }
        }

        profile.setAssignmentTimeProfile(timeProfile);

        if (assignment.getBusinessCommunications() != null && !assignment.getBusinessCommunications().isNull()) {
            try {
                BusinessCommunication businessComm = objectMapper.convertValue(assignment.getBusinessCommunications(), BusinessCommunication.class);
                profile.setBusinessCommunication(businessComm);
            } catch (Exception e) {
                log.error(String.valueOf(e));
            }
        }

        return profile;
    }

    private <T> PageResponse<T> paginateList(List<T> allItems, int page, int size) {
        int totalElements = allItems.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalElements);

        List<T> pageContent = (startIndex < totalElements)
                ? allItems.subList(startIndex, endIndex)
                : List.of();

        return new PageResponse<>(
                pageContent,
                page,
                size,
                totalElements,
                totalPages,
                page == 0,
                page >= totalPages - 1
        );
    }
}
