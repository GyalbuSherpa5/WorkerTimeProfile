package com.example.something.repository;

import com.example.something.model.WorkerAssignmentTimeProfile;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkerAssignmentTimeProfileRepository extends CrudRepository<WorkerAssignmentTimeProfile, UUID> {

    @Query("""
            SELECT watp.*
            FROM wfm_worker_assignment_time_profile watp
             WHERE watp.worker_profile_id IN (:workerProfileIds)
                      AND (:isPrimaryAssignment IS NULL\s
                           OR watp.is_primary_assignment = :isPrimaryAssignment)
            ORDER BY watp.is_primary_assignment DESC, watp.hire_date
            """)
    List<WorkerAssignmentTimeProfile> findByWorkerProfileIds(@Param("workerProfileIds") List<UUID> workerProfileIds,
                                                             @Param("isPrimaryAssignment") Boolean isPrimaryAssignment);
}
