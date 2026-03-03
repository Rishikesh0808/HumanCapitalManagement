package com.project.hcm.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_assignment_leave")
public class EmployeeAssignmentLeave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leave_id", nullable = false)
    private Integer leaveId;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(
            name = "employee_assignment_id",
            nullable = false,
            referencedColumnName = "assignment_id",
            foreignKey = @ForeignKey(name = "employee_assignment_leave_employee_assignment_id_fkey")
    )
    private EmployeeAssignment employeeAssignment;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(
            name = "leave_type_id",
            nullable = false,
            referencedColumnName = "leave_type_id",
            foreignKey = @ForeignKey(name = "employee_assignment_leave_leave_type_id_fkey")
    )
    private LeaveType leaveType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "total_days", nullable = false, precision = 4, scale = 2)
    private BigDecimal totalDays;

    @Column(name = "reason")
    private String reason;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(name = "approved_by")
    private Integer approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    public Integer getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(Integer leaveId) {
        this.leaveId = leaveId;
    }

    public Integer getEmployeeAssignmentId() {
        return employeeAssignment != null ? employeeAssignment.getAssignmentId() : null;
    }

    public void setEmployeeAssignmentId(Integer employeeAssignmentId) {
        if (employeeAssignmentId == null) {
            this.employeeAssignment = null;
            return;
        }
        EmployeeAssignment assignment = new EmployeeAssignment();
        assignment.setAssignmentId(employeeAssignmentId);
        this.employeeAssignment = assignment;
    }

    public Integer getLeaveTypeId() {
        return leaveType != null ? leaveType.getLeaveTypeId() : null;
    }

    public void setLeaveTypeId(Integer leaveTypeId) {
        if (leaveTypeId == null) {
            this.leaveType = null;
            return;
        }
        LeaveType type = new LeaveType();
        type.setLeaveTypeId(leaveTypeId);
        this.leaveType = type;
    }

    public EmployeeAssignment getEmployeeAssignment() {
        return employeeAssignment;
    }

    public void setEmployeeAssignment(EmployeeAssignment employeeAssignment) {
        this.employeeAssignment = employeeAssignment;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
