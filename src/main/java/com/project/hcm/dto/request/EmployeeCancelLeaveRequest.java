package com.project.hcm.dto.request;

public class EmployeeCancelLeaveRequest {
    private Integer leaveId;
    private Integer employeeAssignmentId;

    public Integer getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(Integer leaveId) {
        this.leaveId = leaveId;
    }

    public Integer getEmployeeAssignmentId() {
        return employeeAssignmentId;
    }

    public void setEmployeeAssignmentId(Integer employeeAssignmentId) {
        this.employeeAssignmentId = employeeAssignmentId;
    }
}
