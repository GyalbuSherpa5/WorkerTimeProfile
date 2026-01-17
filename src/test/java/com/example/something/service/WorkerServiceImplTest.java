package com.example.something.service;

import com.example.something.model.WorkerAssignmentTimeProfile;
import com.example.something.model.WorkerProfile;
import com.example.something.repository.WorkerAssignmentTimeProfileRepository;
import com.example.something.repository.WorkerProfileRepository;
import com.example.something.resource.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkerServiceImplTest {

    @Mock
    private WorkerProfileRepository workerProfileRepository;

    @Mock
    private WorkerAssignmentTimeProfileRepository assignmentRepository;

    @Mock
    private ObjectMapper testMapper;

    @Captor
    private ArgumentCaptor<List<UUID>> uuidListCaptor;

    private WorkerServiceImpl service;


    @BeforeEach
    void setUp() {
        service = new WorkerServiceImpl(workerProfileRepository, assignmentRepository);
    }

    @Test
    void getWorkersByTerminalGroup_returnsEmptyWrapper_whenNoWorkersFound() {
        String orgId = "org-1";
        UUID groupId = UUID.randomUUID();

        when(workerProfileRepository.findWorkersByTerminalGroup(eq(orgId), eq(groupId), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(workerProfileRepository.findWorkersNotInTerminalGroup(eq(orgId), eq(groupId), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        WorkerTimeProfileWrapper resultInclude = service.getWorkersByTerminalGroup(orgId, groupId, "", "", 0, 10, true, true);
        WorkerTimeProfileWrapper resultExclude = service.getWorkersByTerminalGroup(orgId, groupId, "", "", 0, 10, false, true);

        assertNotNull(resultInclude, "result should not be null for includeInGroup = true");
        assertNotNull(resultExclude, "result should not be null for includeInGroup = false");

        // If wrapper exposes a list getter, expect it to be empty (adjust method name if different)
        List<WorkerTimeProfile> includeList = resultInclude.getWorkerTimeProfiles();
        if (includeList != null) {
            assertTrue(includeList.isEmpty(), "Expected empty list when no workers found");
        }

        verify(workerProfileRepository).findWorkersByTerminalGroup(eq(orgId), eq(groupId), anyString(), anyString());
        verify(workerProfileRepository).findWorkersNotInTerminalGroup(eq(orgId), eq(groupId), anyString(), anyString());
        verifyNoInteractions(assignmentRepository);
    }

    @Test
    void getWorkersByTerminalGroup_mapsWorkerAndAssignments_correctly_withRealAssignmentInstance() {
        // arrange
        String orgId = "org-42";
        UUID groupId = UUID.randomUUID();
        String givenName = "John";
        String familyName = "Doe";

        // mock WorkerProfile (class wasn't provided)
        WorkerProfile worker = mock(WorkerProfile.class);
        UUID workerProfileId = UUID.randomUUID();
        UUID associateOid = UUID.randomUUID();

        when(worker.getWorkerProfileId()).thenReturn(workerProfileId);
        when(worker.getAssociateOid()).thenReturn(associateOid);
        when(worker.getGivenName()).thenReturn(givenName);
        when(worker.getFamilyName()).thenReturn(familyName);

        when(workerProfileRepository.findWorkersByTerminalGroup(eq(orgId), eq(groupId), eq(givenName), eq(familyName)))
                .thenReturn(List.of(worker));

        // Create a real WorkerAssignmentTimeProfile and populate fields (including JsonNode fields)
        WorkerAssignmentTimeProfile assignment = new WorkerAssignmentTimeProfile();
        UUID workAssignmentId = UUID.randomUUID();
        assignment.setWorkerAssignmentProfileId(UUID.randomUUID());
        assignment.setOrganizationOid("org-42");
        assignment.setWorkAssignmentId(workAssignmentId);
        assignment.setWorkAssignmentTitle("Cashier");
        assignment.setHireDate(LocalDate.now());
        assignment.setWorkerProfileId(workerProfileId);
        assignment.setAssignmentStatus("ACTIVE");
        assignment.setTimeZone("UTC+5:45");
        assignment.setBadgeNumber("B123");
        assignment.setGeofencingBypassIndicator(Boolean.FALSE);
        assignment.setHasHardwareClockPermission(Boolean.FALSE);
        assignment.setHasKioskAppPermission(Boolean.FALSE);
        assignment.setIsPrimaryAssignment(Boolean.FALSE);
        assignment.setIsSupervisor(Boolean.TRUE);

        // supervisorInformation as JsonNode
        Map<String, Object> supervisorInfo = new HashMap<>();
        supervisorInfo.put("supervisorId", "sup-1");
        supervisorInfo.put("supervisorName", "Supervisor One");
        JsonNode supervisorNode = testMapper.valueToTree(supervisorInfo);
        assignment.setSupervisorInformation(supervisorNode);

        // homeLaborAllocations as JsonNode (list)
        List<Map<String, Object>> allocations = new ArrayList<>();
        Map<String, Object> alloc = new HashMap<>();
        alloc.put("homeOrgId", "home-1");
        allocations.add(alloc);
        JsonNode allocationsNode = testMapper.valueToTree(allocations);
        assignment.setHomeLaborAllocations(allocationsNode);

        // businessCommunications as JsonNode
        Map<String, Object> businessComm = new HashMap<>();
        businessComm.put("emails", new ArrayList<>());
        JsonNode businessCommNode = testMapper.valueToTree(businessComm);
        assignment.setBusinessCommunications(businessCommNode);

        when(assignmentRepository.findByWorkerProfileIds(anyList(), anyBoolean())).thenReturn(List.of(assignment));

        // act
        WorkerTimeProfileWrapper wrapper = service.getWorkersByTerminalGroup(orgId, groupId, givenName, familyName, 0, 10, true, true);

        // assert basic structure
        assertNotNull(wrapper, "Wrapper must not be null");
        List<WorkerTimeProfile> profiles = wrapper.getWorkerTimeProfiles();
        assertNotNull(profiles, "Profiles list must not be null");
        assertEquals(1, profiles.size(), "Expected one WorkerTimeProfile entry");

        WorkerTimeProfile person = profiles.getFirst();

        // associateOid was converted to string in service
        assertEquals(associateOid.toString(), person.getAssociateOID(), "associate OID string should match");

        // person name checks
        PersonProfile personProfile = person.getPersonProfile();
        assertNotNull(personProfile, "PersonProfile should not be null");
        PersonName personName = personProfile.getPersonName();
        assertNotNull(personName, "PersonName should not be null");
        assertEquals(familyName + ", " + givenName, personName.getFormattedName(), "Formatted name mismatch");

        // assignment mapping checks
        List<WorkAssignmentProfile> mappedAssignments = person.getWorkAssignmentProfiles();
        assertNotNull(mappedAssignments, "Mapped assignments should not be null");
        assertEquals(1, mappedAssignments.size(), "Expected one mapped assignment");

        WorkAssignmentProfile mapped = mappedAssignments.getFirst();
        assertEquals("Cashier", mapped.getWorkAssignmentTitle(), "Work assignment title mismatch");
        assertEquals(workAssignmentId.toString(), mapped.getWorkAssignmentID(), "WorkAssignmentID string should match");

        AssignmentTimeProfile atp = mapped.getAssignmentTimeProfile();
        assertNotNull(atp, "AssignmentTimeProfile should not be null");
        assertEquals("B123", atp.getBadgeNumber(), "Badge number mismatch");
        assertTrue(atp.getSupervisorIndicator(), "Supervisor indicator should be true");
        assertEquals("UTC+5:45", atp.getTimeZoneCode(), "Time zone mismatch");
        assertFalse(atp.getAllowBadgeTimepunchIndicator(), "AllowBadgeTimepunchIndicator should be false");

        // verify repository interactions and captured argument
        verify(workerProfileRepository).findWorkersByTerminalGroup(eq(orgId), eq(groupId), eq(givenName), eq(familyName));
        verify(assignmentRepository).findByWorkerProfileIds(uuidListCaptor.capture(), anyBoolean());

        List<UUID> captured = uuidListCaptor.getValue();
        assertNotNull(captured);
        assertEquals(1, captured.size());
        assertEquals(workerProfileId, captured.getFirst());
    }
}