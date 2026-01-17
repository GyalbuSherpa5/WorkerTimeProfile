package com.example.something.service;

import com.example.something.resource.WorkerTimeProfileWrapper;

import java.util.UUID;

public interface WorkerService {

    WorkerTimeProfileWrapper getWorkersByTerminalGroup(String orgId, UUID terminalGroupId, String givenName, String familyName, int page, int size, boolean includeInGroup, boolean isPrimaryAssignment);
}
