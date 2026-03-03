package com.project.hcm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "employee_assignment",
        schema = "public",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "employee_assignment_employee_id_start_date_key",
                        columnNames = {"employee_id", "start_date"}
                )
        }
)
public class EmployeeAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id", nullable = false)
    private Integer assignmentId;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "employee_id",
            nullable = false,
            referencedColumnName = "employee_id",
            foreignKey = @ForeignKey(name = "employee_assignment_employee_id_fkey")
    )
    private Employee employee;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "department_id",
            nullable = false,
            referencedColumnName = "department_id",
            foreignKey = @ForeignKey(name = "employee_assignment_department_id_fkey")
    )
    private Department department;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_id", nullable = false, referencedColumnName = "job_id")
    private Job job;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "base_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "is_current")
    private Boolean isCurrent = true;

    @OneToMany(mappedBy = "employeeAssignment")
    private List<EmployeeAssignmentLeave> leaves = new ArrayList<>();

    public Integer getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Integer assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public Boolean getIsCurrent() {
        return isCurrent;
    }

    public void setIsCurrent(Boolean current) {
        isCurrent = current;
    }

    public List<EmployeeAssignmentLeave> getLeaves() {
        return leaves;
    }

    public void setLeaves(List<EmployeeAssignmentLeave> leaves) {
        this.leaves = leaves;
    }
}
