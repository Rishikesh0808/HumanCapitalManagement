package com.project.hcm.controller;

import com.project.hcm.model.EmployeeAssignmentLeave;
import com.project.hcm.model.LeaveType;
import com.project.hcm.dto.ApplyLeaveRequest;
import com.project.hcm.dto.ApplyLeaveResponse;
import com.project.hcm.dto.ApproveLeaveRequest;
import com.project.hcm.dto.ApproveLeaveResponse;
import com.project.hcm.dto.RejectLeaveRequest;
import com.project.hcm.dto.RejectLeaveResponse;
import com.project.hcm.dto.EmployeeCancelLeaveRequest;
import com.project.hcm.dto.CustomResponseDto;
import com.project.hcm.service.EmployeeAssignmentLeaveService;
import com.project.hcm.service.LeaveTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/api/leaves-service")
public class LeaveManagementController {
    private final LeaveTypeService leaveTypeService;
    private final EmployeeAssignmentLeaveService employeeAssignmentLeaveService;

    public LeaveManagementController(LeaveTypeService leaveTypeService,
                                     EmployeeAssignmentLeaveService employeeAssignmentLeaveService) {
        this.leaveTypeService = leaveTypeService;
        this.employeeAssignmentLeaveService = employeeAssignmentLeaveService;
    }

    @GetMapping("/leaveTypes")
    public ResponseEntity<CustomResponseDto> getAllLeaveTypes() {
        try {
            List<LeaveType> leaveTypes = leaveTypeService.getAllLeaveTypes();
            return ResponseEntity.ok(new CustomResponseDto(HttpStatus.OK.value(), leaveTypes));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unexpected error while fetching leave types", ex);
        }
    }

    @PostMapping("/leave")
    public ResponseEntity<CustomResponseDto> addLeave(@RequestBody ApplyLeaveRequest request) {
        try {
            ApplyLeaveResponse leave = employeeAssignmentLeaveService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CustomResponseDto(HttpStatus.CREATED.value(), leave));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unexpected error while creating leave", ex);
        }
    }

    @GetMapping("/leaves/employee/{employeeId}")
    public ResponseEntity<CustomResponseDto> getLeavesByEmployee(@PathVariable Integer employeeId) {
        try {
            List<EmployeeAssignmentLeave> leaves = employeeAssignmentLeaveService.getByEmployeeId(employeeId);
            return ResponseEntity.ok(new CustomResponseDto(HttpStatus.OK.value(), leaves));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unexpected error while fetching employee leaves", ex);
        }
    }

    @PostMapping("/approve")
    public ResponseEntity<CustomResponseDto> approveLeave(@RequestBody ApproveLeaveRequest request) {
        try {
            ApproveLeaveResponse leave = employeeAssignmentLeaveService.approveLeave(request.getLeaveId(), request.getApprovedBy());
            return ResponseEntity.ok(new CustomResponseDto(HttpStatus.OK.value(), leave));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unexpected error while approving leave", ex);
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<CustomResponseDto> rejectLeave(@RequestBody RejectLeaveRequest request) {
        try {
            RejectLeaveResponse leave = employeeAssignmentLeaveService.rejectLeave(
                    request.getLeaveId(),
                    request.getRejectedBy(),
                    request.getRejectionReason()
            );
            return ResponseEntity.ok(new CustomResponseDto(HttpStatus.OK.value(), leave));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unexpected error while rejecting leave", ex);
        }
    }

    @PostMapping("/employee/cancel")
    public ResponseEntity<CustomResponseDto> cancelLeaveByEmployee(@RequestBody EmployeeCancelLeaveRequest request) {
        try {
            EmployeeAssignmentLeave leave = employeeAssignmentLeaveService.cancelLeaveByEmployee(request.getLeaveId(), request.getEmployeeAssignmentId());
            return ResponseEntity.ok(new CustomResponseDto(HttpStatus.OK.value(), leave));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unexpected error while cancelling leave", ex);
        }
    }
}
