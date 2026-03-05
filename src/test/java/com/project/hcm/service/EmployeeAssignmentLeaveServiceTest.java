package com.project.hcm.service;

import com.project.hcm.enums.LeaveStatus;
import com.project.hcm.model.Department;
import com.project.hcm.model.Employee;
import com.project.hcm.model.EmployeeAssignment;
import com.project.hcm.model.EmployeeAssignmentLeave;
import com.project.hcm.repo.EmployeeAssignmentRepository;
import com.project.hcm.repo.EmployeeAssignmentLeaveRepository;
import com.project.hcm.repo.LeaveTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeAssignmentLeaveServiceTest {
    @Mock
    private EmployeeAssignmentLeaveRepository repository;
    @Mock
    private EmployeeAssignmentRepository employeeAssignmentRepository;
    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @InjectMocks
    private EmployeeAssignmentLeaveService service;

    @Test
    void cancelLeaveByEmployee_whenPendingAndOwner_updatesStatus() {
        EmployeeAssignmentLeave leave = new EmployeeAssignmentLeave();
        leave.setLeaveId(1);
        leave.setEmployeeAssignmentId(100);
        leave.setStatus(LeaveStatus.PENDING.getDbValue());
        leave.setApprovedBy(7);
        leave.setApprovedAt(LocalDateTime.now());

        when(repository.findById(1)).thenReturn(Optional.of(leave));
        when(repository.save(any(EmployeeAssignmentLeave.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeAssignmentLeave result = service.cancelLeaveByEmployee(1, 100);

        assertEquals(LeaveStatus.CANCELLED.getDbValue(), result.getStatus());
        assertNull(result.getApprovedBy());
        assertNull(result.getApprovedAt());
        verify(repository).save(leave);
    }

    @Test
    void cancelLeaveByEmployee_whenAlreadyCancelled_returnsAsIs() {
        EmployeeAssignmentLeave leave = new EmployeeAssignmentLeave();
        leave.setLeaveId(2);
        leave.setEmployeeAssignmentId(100);
        leave.setStatus(LeaveStatus.CANCELLED.getDbValue());

        when(repository.findById(2)).thenReturn(Optional.of(leave));

        EmployeeAssignmentLeave result = service.cancelLeaveByEmployee(2, 100);

        assertEquals(LeaveStatus.CANCELLED.getDbValue(), result.getStatus());
        verify(repository, never()).save(any(EmployeeAssignmentLeave.class));
    }

    @Test
    void cancelLeaveByEmployee_whenNotOwner_throwsForbidden() {
        EmployeeAssignmentLeave leave = new EmployeeAssignmentLeave();
        leave.setLeaveId(3);
        leave.setEmployeeAssignmentId(200);
        leave.setStatus(LeaveStatus.PENDING.getDbValue());

        when(repository.findById(3)).thenReturn(Optional.of(leave));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.cancelLeaveByEmployee(3, 100));

        assertEquals(403, ex.getStatusCode().value());
    }

    @Test
    void cancelLeaveByEmployee_whenStatusApproved_throwsConflict() {
        EmployeeAssignmentLeave leave = new EmployeeAssignmentLeave();
        leave.setLeaveId(4);
        leave.setEmployeeAssignmentId(100);
        leave.setStatus(LeaveStatus.APPROVED.getDbValue());

        when(repository.findById(4)).thenReturn(Optional.of(leave));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.cancelLeaveByEmployee(4, 100));

        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void cancelLeaveByEmployee_whenMissingInputs_throwsBadRequest() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.cancelLeaveByEmployee(null, 100));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void approveLeave_whenPendingAndApprovedByManager_updatesStatus() {
        EmployeeAssignmentLeave leave = new EmployeeAssignmentLeave();
        leave.setLeaveId(10);
        leave.setEmployeeAssignmentId(100);
        leave.setStatus(LeaveStatus.PENDING.getDbValue());

        Employee manager = new Employee();
        manager.setEmployeeId(7);
        Department department = new Department();
        department.setManager(manager);
        EmployeeAssignment assignment = new EmployeeAssignment();
        assignment.setAssignmentId(100);
        assignment.setDepartment(department);

        when(repository.findById(10)).thenReturn(Optional.of(leave));
        when(employeeAssignmentRepository.findById(100)).thenReturn(Optional.of(assignment));
        when(repository.save(any(EmployeeAssignmentLeave.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.approveLeave(10, 7);

        assertEquals(LeaveStatus.APPROVED.getDbValue(), leave.getStatus());
        assertEquals(7, leave.getApprovedBy());
        verify(repository).save(leave);
    }

    @Test
    void approveLeave_whenApproverIsNotManager_throwsForbidden() {
        EmployeeAssignmentLeave leave = new EmployeeAssignmentLeave();
        leave.setLeaveId(11);
        leave.setEmployeeAssignmentId(101);
        leave.setStatus(LeaveStatus.PENDING.getDbValue());

        Employee manager = new Employee();
        manager.setEmployeeId(7);
        Department department = new Department();
        department.setManager(manager);
        EmployeeAssignment assignment = new EmployeeAssignment();
        assignment.setAssignmentId(101);
        assignment.setDepartment(department);

        when(repository.findById(11)).thenReturn(Optional.of(leave));
        when(employeeAssignmentRepository.findById(101)).thenReturn(Optional.of(assignment));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.approveLeave(11, 8));

        assertEquals(403, ex.getStatusCode().value());
        verify(repository, never()).save(any(EmployeeAssignmentLeave.class));
    }

    @Test
    void approveLeave_whenLeaveIsNotPending_throwsConflict() {
        EmployeeAssignmentLeave leave = new EmployeeAssignmentLeave();
        leave.setLeaveId(12);
        leave.setEmployeeAssignmentId(102);
        leave.setStatus(LeaveStatus.APPROVED.getDbValue());

        when(repository.findById(12)).thenReturn(Optional.of(leave));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.approveLeave(12, 7));

        assertEquals(409, ex.getStatusCode().value());
        verify(employeeAssignmentRepository, never()).findById(any());
        verify(repository, never()).save(any(EmployeeAssignmentLeave.class));
    }

    @Test
    void rejectLeave_whenPendingAndRejectedByManager_updatesStatus() {
        EmployeeAssignmentLeave leave = new EmployeeAssignmentLeave();
        leave.setLeaveId(13);
        leave.setEmployeeAssignmentId(103);
        leave.setStatus(LeaveStatus.PENDING.getDbValue());

        Employee manager = new Employee();
        manager.setEmployeeId(7);
        Department department = new Department();
        department.setManager(manager);
        EmployeeAssignment assignment = new EmployeeAssignment();
        assignment.setAssignmentId(103);
        assignment.setDepartment(department);

        when(repository.findById(13)).thenReturn(Optional.of(leave));
        when(employeeAssignmentRepository.findById(103)).thenReturn(Optional.of(assignment));
        when(repository.save(any(EmployeeAssignmentLeave.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.rejectLeave(13, 7, "Insufficient balance");

        assertEquals(LeaveStatus.REJECTED.getDbValue(), leave.getStatus());
        assertEquals(7, leave.getApprovedBy());
        assertEquals("Insufficient balance", leave.getRejectionReason());
        assertNotNull(leave.getApprovedAt());
        verify(repository).save(leave);
    }

    @Test
    void rejectLeave_whenRejectedByIsNotManager_throwsForbidden() {
        EmployeeAssignmentLeave leave = new EmployeeAssignmentLeave();
        leave.setLeaveId(14);
        leave.setEmployeeAssignmentId(104);
        leave.setStatus(LeaveStatus.PENDING.getDbValue());

        Employee manager = new Employee();
        manager.setEmployeeId(7);
        Department department = new Department();
        department.setManager(manager);
        EmployeeAssignment assignment = new EmployeeAssignment();
        assignment.setAssignmentId(104);
        assignment.setDepartment(department);

        when(repository.findById(14)).thenReturn(Optional.of(leave));
        when(employeeAssignmentRepository.findById(104)).thenReturn(Optional.of(assignment));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.rejectLeave(14, 8, "Not valid"));

        assertEquals(403, ex.getStatusCode().value());
        verify(repository, never()).save(any(EmployeeAssignmentLeave.class));
    }

    @Test
    void rejectLeave_whenLeaveIsNotPending_throwsConflict() {
        EmployeeAssignmentLeave leave = new EmployeeAssignmentLeave();
        leave.setLeaveId(15);
        leave.setEmployeeAssignmentId(105);
        leave.setStatus(LeaveStatus.APPROVED.getDbValue());

        when(repository.findById(15)).thenReturn(Optional.of(leave));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.rejectLeave(15, 7, "Too late"));

        assertEquals(409, ex.getStatusCode().value());
        verify(employeeAssignmentRepository, never()).findById(any());
        verify(repository, never()).save(any(EmployeeAssignmentLeave.class));
    }
}
