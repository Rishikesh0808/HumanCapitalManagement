package com.project.hcm.service;

import com.project.hcm.dto.EmployeeResponse;
import com.project.hcm.dto.EmployeeUpdateRequest;
import com.project.hcm.model.Employee;
import com.project.hcm.repo.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeResponse getEmployeeById(Integer employeeId) {
        try {
            if (employeeId == null) {
                throw new ResponseStatusException(BAD_REQUEST, "employeeId is required");
            }

            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResponseStatusException(
                            NOT_FOUND, "Employee not found for id: " + employeeId));

            return toResponse(employee);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unexpected error while fetching employee", ex);
        }
    }


    public EmployeeResponse updateEmployee(Integer employeeId, EmployeeUpdateRequest request) {
        try {
            if (employeeId == null) {
                throw new ResponseStatusException(BAD_REQUEST, "employeeId is required");
            }
            if (request == null) {
                throw new ResponseStatusException(BAD_REQUEST, "Request body is required");
            }

            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new ResponseStatusException(
                            NOT_FOUND, "Employee not found for id: " + employeeId));

            validateUpdateRequest(request, employeeId);

            employee.setFirstName(request.getFirstName().trim());
            employee.setLastName(request.getLastName().trim());
            employee.setEmail(request.getEmail().trim());
            employee.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
            employee.setDateOfBirth(request.getDateOfBirth());

            Employee savedEmployee = employeeRepository.save(employee);
            return toResponse(savedEmployee);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unexpected error while updating employee", ex);
        }
    }

    private void validateUpdateRequest(EmployeeUpdateRequest request, Integer employeeId) {
        if (isBlank(request.getFirstName())
                || isBlank(request.getLastName())
                || isBlank(request.getEmail())) {
            throw new ResponseStatusException(BAD_REQUEST, "firstName, lastName and email are required");
        }

        if (employeeRepository.existsByEmailAndEmployeeIdNot(request.getEmail().trim(), employeeId)) {
            throw new ResponseStatusException(CONFLICT, "Email is already in use");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private EmployeeResponse toResponse(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.setEmployeeId(employee.getEmployeeId());
        response.setFirstName(employee.getFirstName());
        response.setLastName(employee.getLastName());
        response.setEmail(employee.getEmail());
        response.setPhone(employee.getPhone());
        response.setDateOfBirth(employee.getDateOfBirth());
        response.setHireDate(employee.getHireDate());
        response.setEmploymentStatus(employee.getEmploymentStatus());
        response.setTerminationDate(employee.getTerminationDate());
        response.setCreatedAt(employee.getCreatedAt());
        return response;
    }
}
