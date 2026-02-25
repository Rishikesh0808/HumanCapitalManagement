package com.project.hcm.service;

import com.project.hcm.enums.LeaveStatus;
import com.project.hcm.model.EmployeeAssignmentLeave;
import com.project.hcm.repo.EmployeeAssignmentLeaveRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeAssignmentLeaveServiceTest {
    @Mock
    private EmployeeAssignmentLeaveRepository repository;

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
    void cancelLeaveByEmployee_whenNotOwner_throwsConflict() {
        EmployeeAssignmentLeave leave = new EmployeeAssignmentLeave();
        leave.setLeaveId(3);
        leave.setEmployeeAssignmentId(200);
        leave.setStatus(LeaveStatus.PENDING.getDbValue());

        when(repository.findById(3)).thenReturn(Optional.of(leave));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.cancelLeaveByEmployee(3, 100));

        assertEquals(409, ex.getStatusCode().value());
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
}
