package com.example.something.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkAssignmentProfile implements Serializable {
    private String workAssignmentID;
    private String workAssignmentTitle;
    private AssignmentStatus assignmentStatus;
    private AssignmentTimeProfile assignmentTimeProfile;
    private BusinessCommunication businessCommunication;
}
