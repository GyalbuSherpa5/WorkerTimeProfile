package com.example.something.repository;

import com.example.something.model.WorkerAssignmentTimeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkerAssignmentTimeProfileRepository extends JpaRepository<WorkerAssignmentTimeProfile, WorkerAssignmentTimeProfile.WorkerAssignmentTimeProfilePK> {

    @Query("SELECT watp FROM WorkerAssignmentTimeProfile watp " +
            "WHERE watp.workerProfileId = :workerProfileId " +
            "AND watp.organizationOid = :orgId")
    List<WorkerAssignmentTimeProfile> findByWorkerProfileIdAndOrgId(
            @Param("workerProfileId") UUID workerProfileId,
            @Param("orgId") String orgId
    );
}
