package com.project.hcm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ApplyLeaveRequest {
    private Integer employeeAssignmentId;
    private Integer leaveTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalDays;
    private String reason;

    public Integer getEmployeeAssignmentId() {
        return employeeAssignmentId;
    }

    public void setEmployeeAssignmentId(Integer employeeAssignmentId) {
        this.employeeAssignmentId = employeeAssignmentId;
    }

    public Integer getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(Integer leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(BigDecimal totalDays) {
        this.totalDays = totalDays;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
