package com.example.something.repository;

import com.example.something.model.WorkerAssignmentTimeProfileLC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkerAssignmentTimeProfileLCRepository extends JpaRepository<WorkerAssignmentTimeProfileLC, WorkerAssignmentTimeProfileLC.WorkerAssignmentTimeProfileLCPK> {

    @Query("SELECT watlc FROM WorkerAssignmentTimeProfileLC watlc " +
            "WHERE watlc.workerAssignmentProfileId = :workerAssignmentProfileId " +
            "AND watlc.organizationOid = :orgId")
    List<WorkerAssignmentTimeProfileLC> findByWorkerAssignmentProfileIdAndOrgId(
            @Param("workerAssignmentProfileId") UUID workerAssignmentProfileId,
            @Param("orgId") String orgId
    );
}
