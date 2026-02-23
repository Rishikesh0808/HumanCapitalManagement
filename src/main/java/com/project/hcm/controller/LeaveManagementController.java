package com.project.hcm.controller;

import com.project.hcm.model.EmployeeAssignmentLeave;
import com.project.hcm.model.LeaveType;
import com.project.hcm.service.EmployeeAssignmentLeaveService;
import com.project.hcm.service.LeaveTypeService;
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
    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeService.getAllLeaveTypes();
    }
    @PostMapping("/leave")
    public EmployeeAssignmentLeave addLeave(@RequestBody EmployeeAssignmentLeave leave) {
        return employeeAssignmentLeaveService.create(leave);
    }

    @GetMapping("/leaves/employee/{employeeId}")
    public List<EmployeeAssignmentLeave> getLeavesByEmployee(@PathVariable Integer employeeId) {
        return employeeAssignmentLeaveService.getByEmployeeId(employeeId);
    }

}
