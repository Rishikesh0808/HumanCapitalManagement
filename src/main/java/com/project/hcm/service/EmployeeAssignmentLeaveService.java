package com.project.hcm.service;

import com.project.hcm.model.EmployeeAssignmentLeave;
import com.project.hcm.enums.LeaveStatus;
import com.project.hcm.dto.request.ApplyLeaveRequest;
import com.project.hcm.repo.EmployeeAssignmentLeaveRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class EmployeeAssignmentLeaveService {
    private final EmployeeAssignmentLeaveRepository employeeAssignmentLeaveRepository;

    public EmployeeAssignmentLeaveService(EmployeeAssignmentLeaveRepository employeeAssignmentLeaveRepository) {
        this.employeeAssignmentLeaveRepository = employeeAssignmentLeaveRepository;
    }

    public EmployeeAssignmentLeave create(ApplyLeaveRequest request) {
        if (request == null) {
            throw new ResponseStatusException(BAD_REQUEST, "Request body is required");
        }

        EmployeeAssignmentLeave leave = new EmployeeAssignmentLeave();
        leave.setEmployeeAssignmentId(request.getEmployeeAssignmentId());
        leave.setLeaveTypeId(request.getLeaveTypeId());
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setTotalDays(request.getTotalDays());
        leave.setReason(request.getReason());
        leave.setStatus(LeaveStatus.PENDING.getDbValue());

        if (leave.getEmployeeAssignmentId() == null
                || leave.getLeaveTypeId() == null
                || leave.getStartDate() == null
                || leave.getEndDate() == null
                || leave.getTotalDays() == null) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "employeeAssignmentId, leaveTypeId, startDate, endDate and totalDays are required");
        }

        if (leave.getAppliedAt() == null) {
            leave.setAppliedAt(LocalDateTime.now());
        }
        return employeeAssignmentLeaveRepository.save(leave);
    }

    public List<EmployeeAssignmentLeave> getByEmployeeId(Integer employeeId) {
        return employeeAssignmentLeaveRepository.findByEmployeeAssignmentId(employeeId);
    }

    public EmployeeAssignmentLeave approveLeave(Integer leaveId, Integer approvedBy) {
        if (leaveId == null || approvedBy == null) {
            throw new ResponseStatusException(BAD_REQUEST, "leaveId and approvedBy are required");
        }

        EmployeeAssignmentLeave leave = employeeAssignmentLeaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Leave not found for id: " + leaveId));

        LeaveStatus currentStatus;
        try {
            currentStatus = LeaveStatus.mapEnumsFromDbValue(leave.getStatus());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(CONFLICT, "Leave has an unsupported status: " + leave.getStatus());
        }

        if (LeaveStatus.APPROVED.equals(currentStatus)) {
            return leave;
        }
        if (!LeaveStatus.PENDING.equals(currentStatus)) {
            throw new ResponseStatusException(CONFLICT, "Only pending leaves can be approved");
        }

        leave.setStatus(LeaveStatus.APPROVED.getDbValue());
        leave.setApprovedBy(approvedBy);
        leave.setApprovedAt(LocalDateTime.now());
        leave.setRejectionReason(null);
        return employeeAssignmentLeaveRepository.save(leave);
    }
}
