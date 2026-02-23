package com.project.hcm.repo;

import com.project.hcm.model.EmployeeAssignmentLeave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeAssignmentLeaveRepository extends JpaRepository<EmployeeAssignmentLeave, Integer> {
    List<EmployeeAssignmentLeave> findByEmployeeAssignmentId(Integer employeeId);
}
