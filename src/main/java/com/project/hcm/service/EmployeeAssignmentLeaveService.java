package com.project.hcm.service;

import com.project.hcm.model.EmployeeAssignmentLeave;
import com.project.hcm.repo.EmployeeAssignmentLeaveRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeAssignmentLeaveService {
    private final EmployeeAssignmentLeaveRepository employeeAssignmentLeaveRepository;

    public EmployeeAssignmentLeaveService(EmployeeAssignmentLeaveRepository employeeAssignmentLeaveRepository) {
        this.employeeAssignmentLeaveRepository = employeeAssignmentLeaveRepository;
    }

    public EmployeeAssignmentLeave create(EmployeeAssignmentLeave leave) {
        if (leave.getStatus() == null || leave.getStatus().isBlank()) {
            leave.setStatus("Pending");
        }
        if (leave.getAppliedAt() == null) {
            leave.setAppliedAt(LocalDateTime.now());
        }
        return employeeAssignmentLeaveRepository.save(leave);
    }

    public List<EmployeeAssignmentLeave> getByEmployeeId(Integer employeeId) {
        return employeeAssignmentLeaveRepository.findByEmployeeAssignmentId(employeeId);
    }
}
