package com.example.something.controller;

import com.example.something.resource.WorkerTimeProfileWrapper;
import com.example.something.service.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/worker-profile")
@RequiredArgsConstructor
public class WorkerTimeProfileController {

    public final WorkerService workerService;

    @GetMapping
    public WorkerTimeProfileWrapper getWorkers(
            @RequestParam String orgId,
            @RequestParam UUID terminalGroupId,
            @RequestParam(required = false) String givenName,
            @RequestParam(required = false) String familyName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "true") boolean includeInGroup,
            @RequestParam(defaultValue = "true") boolean isPrimaryAssignment
            ) {

        return workerService.getWorkersByTerminalGroup(
                orgId, terminalGroupId, givenName, familyName, page, size, includeInGroup, isPrimaryAssignment
        );
    }
}
