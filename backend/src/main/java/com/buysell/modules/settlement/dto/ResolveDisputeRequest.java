package com.buysell.modules.settlement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResolveDisputeRequest {

    @NotBlank
    private String resolution;
    public String getResolution() {
        return this.resolution;
    }
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
