package com.example.something.repository;

import com.example.something.model.WorkerProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkerProfileRepository extends JpaRepository<WorkerProfile, WorkerProfile.WorkerProfilePK> {

    @Query(value = "SELECT DISTINCT wp.* FROM wfm_worker_profile wp " +
            "LEFT JOIN wfm_worker_assignment_time_profile watp ON watp.worker_profile_id = wp.worker_profile_id " +
            "LEFT JOIN wfm_terminalgroup_worker_assignment tgwa ON tgwa.worker_id = wp.worker_profile_id " +
            "WHERE wp.organization_oid = :orgId " +
            "AND wp.associate_oid = :associateId " +
            "AND (CAST(:firstName AS TEXT) IS NULL OR LOWER(CAST(wp.given_name AS TEXT)) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
            "AND (CAST(:lastName AS TEXT) IS NULL OR LOWER(CAST(wp.family_name AS TEXT)) LIKE LOWER(CONCAT('%', :lastName, '%'))) " +
            "AND (:groupId IS NULL OR " +
            "     (:mustInclude = TRUE AND tgwa.terminalgroup_id = :groupId) OR " +
            "     (:mustInclude = FALSE AND (tgwa.terminalgroup_id IS NULL OR tgwa.terminalgroup_id != :groupId)))",
            countQuery = "SELECT COUNT(DISTINCT wp.worker_profile_id) FROM wfm_worker_profile wp " +
                    "LEFT JOIN wfm_worker_assignment_time_profile watp ON watp.worker_profile_id = wp.worker_profile_id " +
                    "LEFT JOIN wfm_terminalgroup_worker_assignment tgwa ON tgwa.worker_id = wp.worker_profile_id " +
                    "WHERE wp.organization_oid = :orgId " +
                    "AND wp.associate_oid = :associateId " +
                    "AND (CAST(:firstName AS TEXT) IS NULL OR LOWER(CAST(wp.given_name AS TEXT)) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
                    "AND (CAST(:lastName AS TEXT) IS NULL OR LOWER(CAST(wp.family_name AS TEXT)) LIKE LOWER(CONCAT('%', :lastName, '%'))) " +
                    "AND (:groupId IS NULL OR " +
                    "     (:mustInclude = TRUE AND tgwa.terminalgroup_id = :groupId) OR " +
                    "     (:mustInclude = FALSE AND (tgwa.terminalgroup_id IS NULL OR tgwa.terminalgroup_id != :groupId)))",
            nativeQuery = true)
    Page<WorkerProfile> findWorkerProfiles(
            @Param("orgId") String orgId,
            @Param("associateId") String associateId,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("groupId") UUID groupId,
            @Param("mustInclude") Boolean mustInclude,
            Pageable pageable
    );
}
