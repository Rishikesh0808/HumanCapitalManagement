package com.project.hcm.service;

import com.project.hcm.model.EmployeeAssignmentLeave;
import com.project.hcm.model.EmployeeAssignment;
import com.project.hcm.model.LeaveType;
import com.project.hcm.enums.LeaveStatus;
import com.project.hcm.dto.ApplyLeaveRequest;
import com.project.hcm.dto.ApplyLeaveResponse;
import com.project.hcm.dto.ApproveLeaveResponse;
import com.project.hcm.dto.RejectLeaveResponse;
import com.project.hcm.repo.EmployeeAssignmentRepository;
import com.project.hcm.repo.EmployeeAssignmentLeaveRepository;
import com.project.hcm.repo.LeaveTypeRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class EmployeeAssignmentLeaveService {
    private final EmployeeAssignmentLeaveRepository employeeAssignmentLeaveRepository;
    private final EmployeeAssignmentRepository employeeAssignmentRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public EmployeeAssignmentLeaveService(
            EmployeeAssignmentLeaveRepository employeeAssignmentLeaveRepository,
            EmployeeAssignmentRepository employeeAssignmentRepository,
            LeaveTypeRepository leaveTypeRepository
    ) {
        this.employeeAssignmentLeaveRepository = employeeAssignmentLeaveRepository;
        this.employeeAssignmentRepository = employeeAssignmentRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public ApplyLeaveResponse create(ApplyLeaveRequest request) {
        try {
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
            leave.setStatus(LeaveStatus.PENDING.getCode());

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

            EmployeeAssignment assignment = employeeAssignmentRepository.findById(request.getEmployeeAssignmentId())
                    .orElseThrow(() -> new ResponseStatusException(
                            NOT_FOUND, "Employee assignment not found for id: " + request.getEmployeeAssignmentId()));
            LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                    .orElseThrow(() -> new ResponseStatusException(
                            NOT_FOUND, "Leave type not found for id: " + request.getLeaveTypeId()));

            leave.setEmployeeAssignment(assignment);
            leave.setLeaveType(leaveType);

            EmployeeAssignmentLeave savedLeave = employeeAssignmentLeaveRepository.save(leave);
            return toApplyLeaveResponse(savedLeave);

        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unexpected error while creating leave", ex);
        }
    }

    public List<EmployeeAssignmentLeave> getByEmployeeId(Integer employeeId) {
        try {
            return employeeAssignmentLeaveRepository.findByEmployeeAssignment_AssignmentId(employeeId);

        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unexpected error while fetching leaves", ex);
        }
    }

    public ApproveLeaveResponse approveLeave(Integer leaveId, Integer approvedBy) {
        try {
            EmployeeAssignmentLeave leave = getEmployeeAssignmentLeave(leaveId, approvedBy);
            checkLeaveStatus(leave);
            verifyManagerID(approvedBy,leave);
            leave.setStatus(LeaveStatus.APPROVED.getCode());
            leave.setApprovedBy(approvedBy);
            leave.setApprovedAt(LocalDateTime.now());
            leave.setRejectionReason(null);
            EmployeeAssignmentLeave savedLeave = employeeAssignmentLeaveRepository.save(leave);
            return toApproveLeaveResponse(savedLeave);
        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unexpected error while approving leave", ex);
        }
    }

    private @NonNull EmployeeAssignmentLeave getEmployeeAssignmentLeave(Integer leaveId, Integer manager) {
        if (leaveId == null || manager== null) {
            throw new ResponseStatusException(BAD_REQUEST, "leaveId and approvedBy are required");
        }
        EmployeeAssignmentLeave leave = employeeAssignmentLeaveRepository.findById(leaveId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Leave not found for id: " + leaveId));
        return leave;
    }

    public RejectLeaveResponse rejectLeave(Integer leaveId, Integer manager, String rejectionReason) {
        try {
            EmployeeAssignmentLeave leave = getEmployeeAssignmentLeave(leaveId, manager);


            checkLeaveStatus(leave);
            verifyManagerID(manager, leave);

            leave.setStatus(LeaveStatus.REJECTED.getCode());
            leave.setApprovedBy(manager);
            leave.setApprovedAt(LocalDateTime.now());
            leave.setRejectionReason(rejectionReason);
            EmployeeAssignmentLeave savedLeave = employeeAssignmentLeaveRepository.save(leave);
            return toRejectLeaveResponse(savedLeave);

        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unexpected error while rejecting leave", ex);
        }
    }

    private void checkLeaveStatus(EmployeeAssignmentLeave leave) {
        LeaveStatus currentStatus;
        try {
            currentStatus = LeaveStatus.fromCode(leave.getStatus());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(CONFLICT, "Leave has an unsupported status: " + leave.getStatus());
        }

        if (!LeaveStatus.PENDING.equals(currentStatus)) {
            throw new ResponseStatusException(CONFLICT, "Only pending leaves can be rejected");
        }
    }

    private void verifyManagerID(Integer manager, EmployeeAssignmentLeave leave) {
        EmployeeAssignment assignment = employeeAssignmentRepository.findById(leave.getEmployeeAssignmentId())
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "Employee assignment not found for id: " + leave.getEmployeeAssignmentId()));
        Integer managerId = assignment.getDepartment() != null && assignment.getDepartment().getManager() != null
                ? assignment.getDepartment().getManager().getEmployeeId()
                : null;
        if (!manager.equals(managerId)) {
            throw new ResponseStatusException(FORBIDDEN, "Only the manager can reject/approve this leave");
        }
    }

    public EmployeeAssignmentLeave cancelLeaveByEmployee(Integer leaveId, Integer employeeAssignmentId) {
        try {
            if (leaveId == null || employeeAssignmentId == null) {
                throw new ResponseStatusException(BAD_REQUEST, "leaveId and employeeAssignmentId are required");
            }

            EmployeeAssignmentLeave leave = employeeAssignmentLeaveRepository.findById(leaveId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Leave not found for id: " + leaveId));

            if (!employeeAssignmentId.equals(leave.getEmployeeAssignmentId())) {
                throw new ResponseStatusException(FORBIDDEN, "You cannot cancel this leave");
            }

            LeaveStatus currentStatus;
            try {
                currentStatus = LeaveStatus.fromCode(leave.getStatus());
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(CONFLICT, "Leave has an unsupported status: " + leave.getStatus());
            }

            if (LeaveStatus.CANCELLED.equals(currentStatus)) {
                return leave;
            }
            if (!LeaveStatus.PENDING.equals(currentStatus)) {
                throw new ResponseStatusException(CONFLICT, "Only pending leaves can be cancelled");
            }

            leave.setStatus(LeaveStatus.CANCELLED.getCode());
            leave.setApprovedBy(null);
            leave.setApprovedAt(null);
            leave.setRejectionReason(null);
            return employeeAssignmentLeaveRepository.save(leave);
        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unexpected error while cancelling leave", ex);
        }
    }

    private ApplyLeaveResponse toApplyLeaveResponse(EmployeeAssignmentLeave leave) {
        ApplyLeaveResponse response = new ApplyLeaveResponse();
        response.setLeaveId(leave.getLeaveId());
        response.setEmployeeAssignmentId(leave.getEmployeeAssignmentId());
        response.setLeaveTypeId(leave.getLeaveTypeId());
        response.setStartDate(leave.getStartDate());
        response.setEndDate(leave.getEndDate());
        response.setTotalDays(leave.getTotalDays());
        response.setReason(leave.getReason());
        response.setStatus(leave.getStatus());
        response.setAppliedAt(leave.getAppliedAt());
        response.setApprovedBy(leave.getApprovedBy());
        response.setApprovedAt(leave.getApprovedAt());
        response.setRejectionReason(leave.getRejectionReason());
        return response;
    }

    private ApproveLeaveResponse toApproveLeaveResponse(EmployeeAssignmentLeave leave) {
        ApproveLeaveResponse response = new ApproveLeaveResponse();
        response.setLeaveId(leave.getLeaveId());
        response.setEmployeeAssignmentId(leave.getEmployeeAssignmentId());
        response.setLeaveTypeId(leave.getLeaveTypeId());
        response.setStartDate(leave.getStartDate());
        response.setEndDate(leave.getEndDate());
        response.setTotalDays(leave.getTotalDays());
        response.setReason(leave.getReason());
        response.setStatus(leave.getStatus());
        response.setAppliedAt(leave.getAppliedAt());
        response.setApprovedBy(leave.getApprovedBy());
        response.setApprovedAt(leave.getApprovedAt());
        response.setRejectionReason(leave.getRejectionReason());
        return response;
    }

    private RejectLeaveResponse toRejectLeaveResponse(EmployeeAssignmentLeave leave) {
        RejectLeaveResponse response = new RejectLeaveResponse();
        response.setLeaveId(leave.getLeaveId());
        response.setEmployeeAssignmentId(leave.getEmployeeAssignmentId());
        response.setLeaveTypeId(leave.getLeaveTypeId());
        response.setStartDate(leave.getStartDate());
        response.setEndDate(leave.getEndDate());
        response.setTotalDays(leave.getTotalDays());
        response.setReason(leave.getReason());
        response.setStatus(leave.getStatus());
        response.setAppliedAt(leave.getAppliedAt());
        response.setRejectedBy(leave.getApprovedBy());
        response.setRejectedAt(leave.getApprovedAt());
        response.setRejectionReason(leave.getRejectionReason());
        return response;
    }
}
