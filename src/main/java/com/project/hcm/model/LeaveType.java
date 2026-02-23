package com.project.hcm.model;

import jakarta.persistence.*;

@Entity
@Table(name = "leave_type")
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="leave_type_id")
    private int leaveTypeId;
    @Column(name = "leave_code", nullable = false, length = 50)
    private String leaveCode;

    @Column(name = "leave_name", nullable = false, length = 200)
    private String leaveName;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "max_days_per_year")
    private Integer maxDaysPerYear;

    @Column(name = "carry_forward_allowed")
    private Boolean carryForwardAllowed;

    @Column(name = "max_carry_forward_days")
    private Integer maxCarryForwardDays;

    @Column(name = "requires_document")
    private Boolean requiresDocument;

    public String getLeaveCode() {
        return leaveCode;
    }

    public void setLeaveCode(String leaveCode) {
        this.leaveCode = leaveCode;
    }

    public String getLeaveName() {
        return leaveName;
    }

    public void setLeaveName(String leaveName) {
        this.leaveName = leaveName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxDaysPerYear() {
        return maxDaysPerYear;
    }

    public void setMaxDaysPerYear(Integer maxDaysPerYear) {
        this.maxDaysPerYear = maxDaysPerYear;
    }

    public Boolean getCarryForwardAllowed() {
        return carryForwardAllowed;
    }

    public void setCarryForwardAllowed(Boolean carryForwardAllowed) {
        this.carryForwardAllowed = carryForwardAllowed;
    }

    public Integer getMaxCarryForwardDays() {
        return maxCarryForwardDays;
    }

    public void setMaxCarryForwardDays(Integer maxCarryForwardDays) {
        this.maxCarryForwardDays = maxCarryForwardDays;
    }

    public Boolean getRequiresDocument() {
        return requiresDocument;
    }

    public void setRequiresDocument(Boolean requiresDocument) {
        this.requiresDocument = requiresDocument;
    }

    public int getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(int leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }
}
