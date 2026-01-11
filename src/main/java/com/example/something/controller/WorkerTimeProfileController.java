package com.example.something.controller;

import com.example.something.resource.WorkerTimeProfileWrapper;
import com.example.something.service.WorkTimeProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/worker-profile")
@RequiredArgsConstructor
public class WorkerTimeProfileController {

    public final WorkTimeProfileService workTimeProfileService;

    @GetMapping
    public WorkerTimeProfileWrapper getWorkerTimeProfile(
            @RequestHeader("org_id") String orgId,
            @RequestHeader("associate_id") String associateId,
            @RequestParam(name="first_name", required = false) String firstName,
            @RequestParam(name="last_name", required = false) String lastName,
            @RequestParam(name="group_id", required = false) UUID groupId,
            @RequestParam(name="must_include_group", required = false) boolean mustIncludeGroup,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return workTimeProfileService.getWorkTimeProfile(associateId,
                orgId,
                firstName,
                lastName,
                groupId,
                mustIncludeGroup,
                page,
                size);
    }
}
