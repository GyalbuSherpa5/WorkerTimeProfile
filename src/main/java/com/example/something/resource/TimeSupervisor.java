package com.example.something.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSupervisor implements Serializable {

    private String associateOID;
    private String workAssignmentID;
    private WorkerId workerId;
    private PersonName personName;
    private String badgeNumber;
    private List<String> roleCodes = new ArrayList<>();
}
