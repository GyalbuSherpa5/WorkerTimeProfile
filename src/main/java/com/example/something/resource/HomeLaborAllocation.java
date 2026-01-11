package com.example.something.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeLaborAllocation implements Serializable {
    private String allocationID;
    private AllocationCode allocationCode;
    private AllocationTypeCode allocationTypeCode;
    private VisibilityScope visibilityScope;
    private Boolean validateIndicator;
    private Boolean requiredIndicator;
}
