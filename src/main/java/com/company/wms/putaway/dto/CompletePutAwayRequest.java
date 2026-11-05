package com.company.wms.putaway.dto;

import jakarta.validation.constraints.Size;

public class CompletePutAwayRequest {

    @Size(max = 128)
    private String completedBy;

    @Size(max = 500)
    private String remarks;

    public CompletePutAwayRequest() {
    }

    public CompletePutAwayRequest(String completedBy, String remarks) {
        this.completedBy = completedBy;
        this.remarks = remarks;
    }

    public String getCompletedBy() {
        return completedBy;
    }

    public void setCompletedBy(String completedBy) {
        this.completedBy = completedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}