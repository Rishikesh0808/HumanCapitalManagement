package com.project.hcm.model;

import jakarta.persistence.*;

@Entity
@Table(name = "department", schema = "public")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(name = "department_name", nullable = false, length = 100)
    private String departmentName;

    @OneToOne
    @JoinColumn(
            name = "manager_id",
            referencedColumnName = "employee_id",
            foreignKey = @ForeignKey(name = "department_manager_id_fkey")
    )
    private Employee manager;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Employee getManager() {
        return manager;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
