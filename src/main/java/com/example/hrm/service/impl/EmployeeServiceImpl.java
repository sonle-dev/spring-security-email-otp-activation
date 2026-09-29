package com.example.hrm.service.impl;

import com.example.hrm.dto.EmployeeCreateDTO;
import com.example.hrm.dto.EmployeeUpdateDTO;
import com.example.hrm.entity.Employee;
import com.example.hrm.repository.EmployeeRepository;
import com.example.hrm.service.CloudinaryService;
import com.example.hrm.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CloudinaryService cloudinaryService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, CloudinaryService cloudinaryService) {
        this.employeeRepository = employeeRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public Employee createEmployee(EmployeeCreateDTO dto) {
        String avatarUrl = cloudinaryService.uploadFile(dto.getAvatarFile());

        Employee employee = Employee.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .department(dto.getDepartment())
                .avatarUrl(avatarUrl)
                .salary(0.0)
                .build();

        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, EmployeeUpdateDTO dto) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên với ID: " + id));

        if (dto.getAvatarFile() != null && !dto.getAvatarFile().isEmpty()) {
            String newAvatarUrl = cloudinaryService.uploadFile(dto.getAvatarFile());
            existingEmployee.setAvatarUrl(newAvatarUrl);
        }

        existingEmployee.setFullName(dto.getFullName());
        existingEmployee.setEmail(dto.getEmail());
        if (dto.getDepartment() != null) {
            existingEmployee.setDepartment(dto.getDepartment());
        }

        return employeeRepository.save(existingEmployee);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên với ID: " + id));
    }
}