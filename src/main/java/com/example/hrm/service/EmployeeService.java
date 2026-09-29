package com.example.hrm.service;

import com.example.hrm.dto.EmployeeCreateDTO;
import com.example.hrm.dto.EmployeeUpdateDTO;
import com.example.hrm.entity.Employee;

import java.util.List;

public interface EmployeeService {
    Employee createEmployee(EmployeeCreateDTO dto);
    Employee updateEmployee(Long id, EmployeeUpdateDTO dto);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
}