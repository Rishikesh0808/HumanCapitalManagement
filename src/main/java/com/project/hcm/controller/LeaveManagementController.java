package com.project.hcm.controller;

import com.project.hcm.model.EmployeeAssignmentLeave;
import com.project.hcm.model.LeaveType;
import com.project.hcm.dto.ApplyLeaveRequest;
import com.project.hcm.dto.ApproveLeaveRequest;
import com.project.hcm.dto.EmployeeCancelLeaveRequest;
import com.project.hcm.dto.CustomResponseDto;
import com.project.hcm.service.EmployeeAssignmentLeaveService;
import com.project.hcm.service.LeaveTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        List<LeaveType> leaveTypes = leaveTypeService.getAllLeaveTypes();
        return ResponseEntity.ok(new CustomResponseDto(HttpStatus.OK.value(), leaveTypes));
    }

    @PostMapping("/leave")
    public ResponseEntity<CustomResponseDto> addLeave(@RequestBody ApplyLeaveRequest request) {
        EmployeeAssignmentLeave leave = employeeAssignmentLeaveService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CustomResponseDto(HttpStatus.CREATED.value(), leave));
    }

    @GetMapping("/leaves/employee/{employeeId}")
    public ResponseEntity<CustomResponseDto> getLeavesByEmployee(@PathVariable Integer employeeId) {
        List<EmployeeAssignmentLeave> leaves = employeeAssignmentLeaveService.getByEmployeeId(employeeId);
        return ResponseEntity.ok(new CustomResponseDto(HttpStatus.OK.value(), leaves));
    }

    @PostMapping("/approve")
    public ResponseEntity<CustomResponseDto> approveLeave(@RequestBody ApproveLeaveRequest request) {
        EmployeeAssignmentLeave leave = employeeAssignmentLeaveService.approveLeave(request.getLeaveId(), request.getApprovedBy());
        return ResponseEntity.ok(new CustomResponseDto(HttpStatus.OK.value(), leave));
    }

    @PostMapping("/employee/cancel")
    public ResponseEntity<CustomResponseDto> cancelLeaveByEmployee(@RequestBody EmployeeCancelLeaveRequest request) {
        EmployeeAssignmentLeave leave = employeeAssignmentLeaveService.cancelLeaveByEmployee(request.getLeaveId(), request.getEmployeeAssignmentId());
        return ResponseEntity.ok(new CustomResponseDto(HttpStatus.OK.value(), leave));
    }
}
