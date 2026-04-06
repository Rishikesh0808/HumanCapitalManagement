package com.project.hcm.repo;

import com.project.hcm.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    boolean existsByEmailAndEmployeeIdNot(String email, Integer employeeId);
}
