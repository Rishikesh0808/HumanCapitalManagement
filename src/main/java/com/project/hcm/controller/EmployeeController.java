package com.project.hcm.controller;

import com.project.hcm.dto.CustomResponseDto;
import com.project.hcm.dto.EmployeeResponse;
import com.project.hcm.dto.EmployeeUpdateRequest;
import com.project.hcm.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/{employeeId}")
    public ResponseEntity<CustomResponseDto> getEmployee(@PathVariable Integer employeeId) {
        try {
            EmployeeResponse employee = employeeService.getEmployeeById(employeeId);
            return ResponseEntity.ok(new CustomResponseDto(HttpStatus.OK.value(), employee));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {

            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unexpected error while fetching employee", ex);
        }
    }

    @PutMapping("/{employeeId}")
    public ResponseEntity<CustomResponseDto> updateEmployee(
            @PathVariable Integer employeeId,
            @RequestBody EmployeeUpdateRequest request
    ) {
        try {
            EmployeeResponse employee = employeeService.updateEmployee(employeeId, request);
            return ResponseEntity.ok(new CustomResponseDto(HttpStatus.OK.value(), employee));
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "Unexpected error while updating employee", ex);
        }
    }
}
