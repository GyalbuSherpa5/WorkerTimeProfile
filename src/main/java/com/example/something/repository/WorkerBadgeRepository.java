package com.example.something.repository;

import com.example.something.model.WorkerBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkerBadgeRepository extends JpaRepository<WorkerBadge, WorkerBadge.WorkerBadgePK> {

    @Query("SELECT wb FROM WorkerBadge wb " +
            "WHERE wb.workAssignmentId = :workAssignmentId " +
            "AND wb.organizationOid = :orgId")
    Optional<WorkerBadge> findByWorkAssignmentIdAndOrgId(
            @Param("workAssignmentId") String workAssignmentId,
            @Param("orgId") String orgId
    );
}
