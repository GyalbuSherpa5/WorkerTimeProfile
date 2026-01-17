package com.example.something.repository;

import com.example.something.model.WorkerProfile;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkerProfileRepository extends CrudRepository<WorkerProfile, UUID> {

    @Query("""
            SELECT DISTINCT wp.*
            FROM wfm_worker_profile wp
            INNER JOIN wfm_terminalgroup_worker_assignment tgwa 
                ON wp.worker_id = tgwa.worker_id
            WHERE wp.organization_oid = :orgId
                AND tgwa.terminalgroup_id = :terminalGroupId
                AND (:givenName IS NULL OR wp.given_name ILIKE CONCAT('%', :givenName, '%'))
                AND (:familyName IS NULL OR wp.family_name ILIKE CONCAT('%', :familyName, '%'))
            ORDER BY wp.given_name, wp.family_name
            """)
    List<WorkerProfile> findWorkersByTerminalGroup(
            @Param("orgId") String orgId,
            @Param("terminalGroupId") UUID terminalGroupId,
            @Param("givenName") String givenName,
            @Param("familyName") String familyName
    );

    // Find workers NOT in terminal group (NOT EQUALS TO)
    @Query("""
            SELECT DISTINCT wp.*
            FROM wfm_worker_profile wp
            WHERE wp.organization_oid = :orgId
                AND wp.worker_id NOT IN (
                    SELECT worker_id 
                    FROM wfm_terminalgroup_worker_assignment 
                    WHERE terminalgroup_id = :terminalGroupId
                )
                AND (:givenName IS NULL OR wp.given_name ILIKE CONCAT('%', :givenName, '%'))
                AND (:familyName IS NULL OR wp.family_name ILIKE CONCAT('%', :familyName, '%'))
            ORDER BY wp.given_name, wp.family_name
            """)
    List<WorkerProfile> findWorkersNotInTerminalGroup(
            @Param("orgId") String orgId,
            @Param("terminalGroupId") UUID terminalGroupId,
            @Param("givenName") String givenName,
            @Param("familyName") String familyName
    );
}

