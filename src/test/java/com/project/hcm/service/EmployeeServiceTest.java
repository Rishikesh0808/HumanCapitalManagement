package com.project.hcm.service;

import com.project.hcm.dto.EmployeeResponse;
import com.project.hcm.dto.EmployeeUpdateRequest;
import com.project.hcm.model.Employee;
import com.project.hcm.repo.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    private Employee e;
    private EmployeeUpdateRequest request;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee e1

    @BeforeEach()
    void beforeAll() {
        e = new Employee();
        e.setEmployeeId(1);
        e.setFirstName("firstName");
        e.setLastName("lastName");
        e.setEmail("old@mail.com");
        e.setPhone("9999999999");
        e.setDateOfBirth(LocalDate.of(2000, 1, 1));

        request = new EmployeeUpdateRequest();
        request.setFirstName(" John ");
        request.setLastName(" Doe ");
        request.setEmail(" john@mail.com ");
        request.setPhone(" 9876543210 ");
        request.setDateOfBirth(LocalDate.of(1998, 5, 20));
    }

    @Test
    public void getEmployeeByIDShouldReturnEmployeeByID() {
        when(employeeRepository.findById(1)).thenReturn(Optional.ofNullable(e));
        EmployeeResponse eTest = employeeService.getEmployeeById(1);
        EmployeeResponse response = getDummyResponse();
        assertEquals(eTest.getEmployeeId(), response.getEmployeeId());
        assertEquals(eTest.getFirstName(), response.getFirstName());
        assertEquals(eTest.getLastName(), response.getLastName());
        verify(employeeRepository).findById(1);
    }

    @Test
    public void getEmployeeByIDShouldReturnNull() {
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(
                RuntimeException.class,
                () -> employeeService.getEmployeeById(1)
        );
    }

    @Test
    public void updateEmployeeShouldThrowBadRequestWhenEmployeeIdIsNull() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> employeeService.updateEmployee(null, request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    public void updateEmployeeShouldThrowNotFoundWhenEmployeeDoesNotExist() {
        when(employeeRepository.findById(1)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> employeeService.updateEmployee(1, request)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    public void updateEmployeeShouldThrowConflictWhenEmailAlreadyExists() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(e));
        when(employeeRepository.existsByEmailAndEmployeeIdNot("john@mail.com", 1)).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> employeeService.updateEmployee(1, request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    public void updateEmployeeShouldUpdateEmployeeSuccessfully() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(e));
        when(employeeRepository.existsByEmailAndEmployeeIdNot("john@mail.com", 1)).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeResponse response = employeeService.updateEmployee(1, request);

        assertEquals(1, response.getEmployeeId());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john@mail.com", response.getEmail());
        assertEquals("9876543210", response.getPhone());
        assertEquals(LocalDate.of(1998, 5, 20), response.getDateOfBirth());
        verify(employeeRepository).save(e);
    }

    private EmployeeResponse getDummyResponse() {
        EmployeeResponse response = new EmployeeResponse();
        response.setEmployeeId(1);
        response.setFirstName("firstName");
        response.setLastName("lastName");
        return response;
    }
}
