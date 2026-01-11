package com.example.something.service;

import com.example.something.model.WorkerAssignmentTimeProfile;
import com.example.something.model.WorkerAssignmentTimeProfileLC;
import com.example.something.model.WorkerBadge;
import com.example.something.model.WorkerProfile;
import com.example.something.repository.WorkerAssignmentTimeProfileLCRepository;
import com.example.something.repository.WorkerAssignmentTimeProfileRepository;
import com.example.something.repository.WorkerBadgeRepository;
import com.example.something.repository.WorkerProfileRepository;
import com.example.something.resource.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkerTimeProfileServiceImpl implements WorkTimeProfileService {

    private final WorkerBadgeRepository workerBadgeRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final WorkerAssignmentTimeProfileRepository workerAssignmentTimeProfileRepository;
    private final WorkerAssignmentTimeProfileLCRepository workerAssignmentTimeProfileLCRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public WorkerTimeProfileWrapper getWorkTimeProfile(
            String associateId,
            String orgId,
            String firstName,
            String lastName,
            UUID groupId,
            boolean mustIncludeGroup,
            int page,
            int size) {

        // Create pageable
        Pageable pageable = PageRequest.of(page, size);

        // Fetch worker profiles with filters
        Page<WorkerProfile> workerProfilePage = workerProfileRepository.findWorkerProfiles(
                orgId, associateId, firstName, lastName, groupId, mustIncludeGroup, pageable
        );

        // Map to DTOs
        List<WorkerTimeProfile> workerTimeProfiles = workerProfilePage.getContent()
                .stream()
                .map(wp -> mapToWorkerTimeProfile(wp, orgId))
                .collect(Collectors.toList());

        return new WorkerTimeProfileWrapper(workerTimeProfiles);
    }

    private WorkerTimeProfile mapToWorkerTimeProfile(WorkerProfile workerProfile, String orgId) {
        WorkerTimeProfile dto = new WorkerTimeProfile();

        // Set associateOID
        dto.setAssociateOID(workerProfile.getAssociateOid());

        // Map PersonProfile
        dto.setPersonProfile(mapToPersonProfile(workerProfile));

        // Map WorkAssignmentProfiles
        dto.setWorkAssignmentProfiles(mapToWorkAssignmentProfiles(workerProfile, orgId));

        return dto;
    }

    private PersonProfile mapToPersonProfile(WorkerProfile workerProfile) {
        PersonProfile personProfile = new PersonProfile();

        PersonName personName = new PersonName();
        personName.setGivenName(workerProfile.getGivenName());
        personName.setFamilyName(workerProfile.getFamilyName());

        // Create formatted name
        String formattedName = createFormattedName(
                workerProfile.getGivenName(),
                workerProfile.getFamilyName()
        );
        personName.setFormattedName(formattedName);

        personProfile.setPersonName(personName);

        return personProfile;
    }

    private String createFormattedName(String givenName, String familyName) {
        StringBuilder formatted = new StringBuilder();
        if (givenName != null && !givenName.isEmpty()) {
            formatted.append(givenName);
        }
        if (familyName != null && !familyName.isEmpty()) {
            if (!formatted.isEmpty()) {
                formatted.append(" ");
            }
            formatted.append(familyName);
        }
        return formatted.toString();
    }

    private List<WorkAssignmentProfile> mapToWorkAssignmentProfiles(
            WorkerProfile workerProfile, String orgId) {

        // Fetch worker assignment time profiles
        List<WorkerAssignmentTimeProfile> assignments =
                workerAssignmentTimeProfileRepository.findByWorkerProfileIdAndOrgId(
                        workerProfile.getWorkerProfileId(), orgId
                );

        return assignments.stream()
                .map(assignment -> mapToWorkAssignmentProfile(assignment, orgId))
                .collect(Collectors.toList());
    }

    private WorkAssignmentProfile mapToWorkAssignmentProfile(
            WorkerAssignmentTimeProfile assignment, String orgId) {

        WorkAssignmentProfile dto = new WorkAssignmentProfile();

        // Set work assignment ID
        dto.setWorkAssignmentID(assignment.getWorkAssignmentId());

        // Set assignment status
        AssignmentStatus status = new AssignmentStatus();
        status.setCode(assignment.getStatus());
        dto.setAssignmentStatus(status);

        // Map assignment time profile
        dto.setAssignmentTimeProfile(mapToAssignmentTimeProfile(assignment, orgId));

        // Map business communication
        dto.setBusinessCommunication(mapToBusinessCommunication(assignment));

        return dto;
    }

    private AssignmentTimeProfile mapToAssignmentTimeProfile(
            WorkerAssignmentTimeProfile assignment, String orgId) {

        AssignmentTimeProfile dto = new AssignmentTimeProfile();

        // Set time zone code
        dto.setTimeZoneCode(assignment.getTimeZone());

        // Set time entry plan ID
        dto.setTimeEntryPlanID(assignment.getTimeEntryPlanId());

        // Set badge number
        Optional<WorkerBadge> badge = workerBadgeRepository.findByWorkAssignmentIdAndOrgId(
                assignment.getWorkAssignmentId(), orgId
        );
        badge.ifPresent(b -> dto.setBadgeNumber(b.getBadgeNumber()));

        // Set allow badge time-punch indicator
        dto.setAllowBadgeTimepunchIndicator(assignment.getHasHardwareClockPermission());

        // Map home labor allocations
        dto.setHomeLaborAllocations(mapToHomeLaborAllocations(assignment, orgId));

        return dto;
    }

    private List<HomeLaborAllocation> mapToHomeLaborAllocations(
            WorkerAssignmentTimeProfile assignment, String orgId) {

        // Fetch labor allocations
        List<WorkerAssignmentTimeProfileLC> allocations =
                workerAssignmentTimeProfileLCRepository.findByWorkerAssignmentProfileIdAndOrgId(
                        assignment.getWorkerAssignmentProfileId(), orgId
                );

        return allocations.stream()
                .map(this::mapToHomeLaborAllocation)
                .collect(Collectors.toList());
    }

    private HomeLaborAllocation mapToHomeLaborAllocation(
            WorkerAssignmentTimeProfileLC allocation) {

        HomeLaborAllocation dto = new HomeLaborAllocation();

        // Set allocation code
        AllocationCode allocationCode = new AllocationCode();
        allocationCode.setCode(allocation.getAllocationCode());
        allocationCode.setName(allocation.getAllocationName());
        dto.setAllocationCode(allocationCode);

        // Set allocation type code
        AllocationTypeCode allocationTypeCode = new AllocationTypeCode();
        allocationTypeCode.setCode(allocation.getAllocationTypeCode());
        allocationTypeCode.setName(allocation.getAllocationTypeName());
        dto.setAllocationTypeCode(allocationTypeCode);

        return dto;
    }

    private BusinessCommunication mapToBusinessCommunication(
            WorkerAssignmentTimeProfile assignment) {

        BusinessCommunication dto = new BusinessCommunication();

        // Parse business communications JSON if exists
        if (assignment.getBusinessCommunications() != null) {
            try {
                Map<String, Object> commData = objectMapper.readValue(
                        assignment.getBusinessCommunications(),
                        new TypeReference<>() {
                        }
                );

                // Extract emails if present in JSON
                List<Email> emails = extractEmailsFromJson(commData);
                dto.setEmails(emails);

            } catch (Exception e) {
                log.warn("Failed to parse business communications JSON", e);
                dto.setEmails(new ArrayList<>());
            }
        } else {
            dto.setEmails(new ArrayList<>());
        }

        return dto;
    }

    private List<Email> extractEmailsFromJson(Map<String, Object> commData) {
        List<Email> emails = new ArrayList<>();

        // Assuming JSON structure has an "emails" array
        if (commData.containsKey("emails")) {
            Object emailsObj = commData.get("emails");
            if (emailsObj instanceof List<?> emailList) {
                for (Object emailObj : emailList) {
                    if (emailObj instanceof Map<?, ?> emailMap) {
                        Email email = new Email();

                        if (emailMap.containsKey("emailUri")) {
                            email.setEmailUri((String) emailMap.get("emailUri"));
                        }

                        if (emailMap.containsKey("nameCode")) {
                            Map<?, ?> nameCodeMap = (Map<?, ?>) emailMap.get("nameCode");
                            NameCode nameCode = new NameCode();
                            nameCode.setCode((String) nameCodeMap.get("code"));
                            nameCode.setName((String) nameCodeMap.get("name"));
                            email.setNameCode(nameCode);
                        }

                        emails.add(email);
                    }
                }
            }
        }

        return emails;
    }
}
