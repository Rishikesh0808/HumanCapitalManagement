package com.project.hcm.repo;

import com.project.hcm.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Integer> {
}
