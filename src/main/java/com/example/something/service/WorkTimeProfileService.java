package com.example.something.service;

import com.example.something.resource.WorkerTimeProfileWrapper;

import java.util.UUID;

public interface WorkTimeProfileService {

    WorkerTimeProfileWrapper getWorkTimeProfile(String associateId, String orgId, String firstName, String lastName, UUID groupId, boolean mustIncludeGroup, int page, int size);
}
