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
public class WorkerTimeProfileWrapper implements Serializable {
    private List<WorkerTimeProfile> workerTimeProfiles = new ArrayList<>();
}
