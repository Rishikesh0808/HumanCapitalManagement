package com.project.hcm.repo;

import com.project.hcm.model.EmployeeAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeAssignmentRepository extends JpaRepository<EmployeeAssignment, Integer> {
}
